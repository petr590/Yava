package x590.yava.io;

import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import x590.util.*;
import x590.util.annotation.Nullable;
import x590.util.function.ObjIntBooleanFunction;
import x590.util.io.UncheckedInputStream;
import x590.yava.Keywords;
import x590.yava.attribute.code.CodeAttribute;
import x590.yava.constpool.Constant;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.MethodHandleConstant.ReferenceKind;
import x590.yava.constpool.NameAndTypeConstant;
import x590.yava.constpool.ReferenceConstant;
import x590.yava.exception.parsing.ParseException;
import x590.yava.type.Type;
import x590.yava.type.Types;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ArrayType;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.RealReferenceType;
import x590.yava.type.reference.ReferenceType;
import x590.yava.type.reference.generic.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static x590.yava.Keywords.*;

public class AssemblingInputStream extends UncheckedInputStream {

	private final InputStream in;

	/**
	 * Буферизация. Необходима, так как мы не можем посмотреть следующие несколько символов, не прочитав их.
	 */
	private @Nullable String nextBufferedString;

	private int nextBufferedCharPos;

	private void buffer(String str) {
		String nextBufferedString = this.nextBufferedString;

		this.nextBufferedString = nextBufferedString == null ?
				str :
				str + nextBufferedString.substring(nextBufferedCharPos);

		nextBufferedCharPos = 0;
	}

	private void buffer(int ch) {
		buffer(String.valueOf((char)ch));
	}

	public AssemblingInputStream(InputStream in) {
		this.in = in;
	}

	/**
	 * @return Адрес следующей метки.
	 * Если метка ещё не была объявлена, возвращает 0 и добавляет запись в {@code labelsManager}
	 * о том, что эта метка должна быть инициализирована позже
	 */
	public int nextLabel(CodeAttribute.LabelsManager labelsManager) {
		String name = nextString();

		if (labelsManager.hasLabel(name)) {
			return labelsManager.getLabelPos(name) - labelsManager.getInstructionPos();
		}

		labelsManager.addForwardingEntry(name);

		return 0;
	}


	/**
	 * Предикат, который проверяет, является ли символ началом или частью выражения
	 */
	private record CharPredicate(IntPredicate startPredicate, IntPredicate partPredicate) {

		public static final CharPredicate
				STRING = new CharPredicate(Character::isJavaIdentifierPart, Character::isJavaIdentifierPart),
				NAME = new CharPredicate(Character::isJavaIdentifierStart, Character::isJavaIdentifierPart),
				TYPE = new CharPredicate(CharPredicate::isJavaTypeStart, CharPredicate::isJavaTypePart),
				GENERIC_DECLARATION_TYPE = STRING,
				INTEGRAL_NUMBER = new CharPredicate(CharPredicate::isIntegralNumberStart, CharPredicate::isIntegralNumberPart),
				ANY_NUMBER = new CharPredicate(CharPredicate::isAnyNumberStart, CharPredicate::isAnyNumberPart);

		/**
		 * @return Предикат, который позволяет читать
		 * не больше символов, чем указано в {@code maxCount}
		 */
		public static CharPredicate withLength(int maxCount) {
			var predicate = new IntPredicate() {
				private int count;

				@Override
				public boolean test(int value) {
					return ++count <= maxCount;
				}
			};

			return new CharPredicate(predicate, predicate);
		}

		public boolean isStartChar(int ch) {
			return startPredicate.test(ch);
		}

		public boolean isPartChar(int ch) {
			return partPredicate.test(ch);
		}


		private static boolean isJavaTypeStart(int ch) {
			return Character.isJavaIdentifierStart(ch);
		}

		private static boolean isJavaTypePart(int ch) {
			return Character.isJavaIdentifierPart(ch) || ch == '.';
		}

		private static boolean isIntegralNumberStart(int ch) {
			return ch == '-' || isIntegralNumberPart(ch) || ch == '+';
		}

		private static boolean isIntegralNumberPart(int ch) {
			return Character.isJavaIdentifierPart(ch);
		}

		private static boolean isAnyNumberStart(int ch) {
			return isIntegralNumberStart(ch) || ch == '.';
		}

		private static boolean isAnyNumberPart(int ch) {
			return isIntegralNumberPart(ch) || ch == '.';
		}
	}

	/**
	 * Обрабатывает случаи, когда символ не соответствует ожидаемому,
	 * а также когда достигнут конец потока
	 */
	private record NonMatchingCharHandler(
			Char2ObjectFunction<String> nonMatchingCharHandler,
			Supplier<String> eofHandler
	) {

		private static final Map<String, NonMatchingCharHandler> THROWING_HANDLERS = new HashMap<>();

		public static final NonMatchingCharHandler DEFAULT_HANDLER = new NonMatchingCharHandler(String::valueOf, () -> "");

		public static NonMatchingCharHandler throwingParseException(String expected) {
			return THROWING_HANDLERS.computeIfAbsent(expected,
					exp -> new NonMatchingCharHandler(
							actual -> {
								throw ParseException.expectedButGot(exp, String.valueOf(actual));
							},
							() -> {
								throw ParseException.expectedButGotEof(exp);
							}
					)
			);
		}

		public String handleNonMatchingChar(char ch) {
			return nonMatchingCharHandler.get(ch);
		}

		public String handleEof() {
			return eofHandler.get();
		}
	}


	@Override
	public int read() throws UncheckedIOException {
		var nextBufferedString = this.nextBufferedString;

		if (nextBufferedString != null) {
			if (nextBufferedCharPos < nextBufferedString.length()) {
				return nextBufferedString.charAt(nextBufferedCharPos++);
			} else {
				this.nextBufferedString = null;
			}
		}

		try {
			return in.read();
		} catch (IOException ex) {
			throw newUncheckedException(ex);
		}
	}


	/**
	 * @return Следующий непробельный символ или {@link #EOF_CHAR}, если данные закончились
	 */
	public int next() {
		int ch;

		do {
			ch = read();
		} while (Character.isWhitespace(ch));


		if (ch == '/') {
			int nextCh = read();

			if (nextCh == '/') { // однострочный комментарий
				do {
					ch = read();
				} while (ch != '\n');

				return next();

			} else if (nextCh == '*') { // многострочный комментарий

				nextCh = read();

				do {
					ch = nextCh;
					nextCh = read();
				} while (ch != '*' || nextCh != '/');

				return next();

			} else {
				buffer(nextCh);
			}
		}

		return ch;
	}


	/**
	 * @return Следующую строку, которая содержит либо название, либо другой непробельный символ
	 */
	public String nextString() {
		return nextString(CharPredicate.STRING, NonMatchingCharHandler.DEFAULT_HANDLER);
	}

	/**
	 * @return Следующую строку, которая содержит название
	 */
	public String nextName() {
		String str = tryReadStringLiteral();

		if (str != null) {
			return str;
		}

		return nextString(CharPredicate.NAME, NonMatchingCharHandler.throwingParseException("name"));
	}

	/**
	 * @return Следующий тип
	 */
	public Type nextType() {
		return nextType(
				NonMatchingCharHandler.throwingParseException("type name"),
				name -> { throw ParseException.expectedButGot("type name", name); }
		);
	}

	/**
	 * @return Следующий тип или {@code null}, если его нет
	 */
	public @Nullable Type nextTypeIfExists() {
		return nextType(
				new NonMatchingCharHandler(
					ch -> {
						buffer(ch);
						return null;
					},
					() -> null
				),
				name -> {
					buffer(name);
					return null;
				}
		);
	}

	private @Nullable Type nextType(NonMatchingCharHandler nonMatchingCharHandler, Function<String, Type> nonMatchingNameHandler) {
		String name = nextString(CharPredicate.TYPE, nonMatchingCharHandler);

		if (name == null) {
			return null;
		}

		int nestingLevel = 0;

		for (; advanceIfHasNext('['); nestingLevel++) {
			if (!advanceIfHasNext(']')) {
				buffer('[');
				break;
			}
		}

		String[] parts = name.split("\\.");

		for (int i = 0, size = parts.length; i < size; i++) {
			String part = parts[i];

			if (Keywords.isKeyword(part)) {
				int cutIndex = i;

				if (i == 0) {
					if (!Keywords.isPrimitive(part)) {
						return nonMatchingNameHandler.apply(name);
					}

					cutIndex += 1;
				}

				name = Arrays.stream(parts, 0, cutIndex).collect(Collectors.joining("."));
				buffer(Arrays.stream(parts, cutIndex, parts.length).map(str -> '.' + str).collect(Collectors.joining()));

				break;
			}
		}

		Type type = switch (name) {
			case BYTE    -> PrimitiveType.BYTE;
			case SHORT   -> PrimitiveType.SHORT;
			case CHAR    -> PrimitiveType.CHAR;
			case INT     -> PrimitiveType.INT;
			case LONG    -> PrimitiveType.LONG;
			case FLOAT   -> PrimitiveType.FLOAT;
			case DOUBLE  -> PrimitiveType.DOUBLE;
			case BOOLEAN -> PrimitiveType.BOOLEAN;
			case VOID    -> PrimitiveType.VOID;
			default      -> ClassType.fromDescriptor(name.replace('.', '/'));
		};

		return nestingLevel == 0 ? type : type.arrayTypeAsType(nestingLevel);
	}

	/** @return Следующий ссылочный тип */
	public RealReferenceType nextReferenceType() {
		return castOrThrowParseException(nextType(), RealReferenceType.class);
	}

	/** @return Следующий тип класса */
	public ClassType nextClassType() {
		return castOrThrowParseException(nextType(), ClassType.class);
	}

	/** @return Следующий тип массива */
	public ArrayType nextArrayType() {
		return castOrThrowParseException(nextType(), ArrayType.class);
	}

	/** @return Следующий примитивный тип */
	public PrimitiveType nextPrimitiveType() {
		return castOrThrowParseException(nextType(), PrimitiveType.class);
	}


	public @Nullable ClassType nextNullableClassType() {
		return advanceIfHasNext(NULL) ? null : nextClassType();
	}


	/**
	 * @return Следующий тип, который может быть параметризован
	 */
	public Type nextParametrizedType() {
		Type type = nextType();

		if (type instanceof ClassType classType) {
			if (advanceIfHasNext('<')) {
				var parameters = GenericParameters.of(nextParameterTypesStream().toList());
				requireNext('>');
				return classType.withSignature(parameters);
			}
		}

		return type;
	}

	/**
	 * @return Следующий ссылочный тип, который может быть параметризован
	 */
	public RealReferenceType nextParametrizedReferenceType() {
		return castOrThrowParseException(nextParametrizedType(), RealReferenceType.class);
	}

	/**
	 * @return Следующий тип, представляющий параметр дженерика
	 */
	public ReferenceType nextParameterType() {
		if (advanceIfHasNext('?')) {
			if (advanceIfHasNext(EXTENDS)) {
				return new ExtendingGenericType(nextParametrizedReferenceType());
			}

			if (advanceIfHasNext(SUPER)) {
				return new SuperGenericType(nextParametrizedReferenceType());
			}

			return Types.ANY_GENERIC_TYPE;
		}

		return nextParametrizedReferenceType();
	}

	/**
	 * @return Следующий тип, представляющий объявление дженерика
	 */
	public GenericDeclarationType nextGenericDeclarationType() {
		String name = nextString(CharPredicate.GENERIC_DECLARATION_TYPE, NonMatchingCharHandler.throwingParseException("generic type name"));

		if (Keywords.isKeyword(name)) {
			throw ParseException.expectedButGot("generic type", name);
		}

		List<ReferenceType> types;

		if (advanceIfHasNext(EXTENDS)) {
			types = new ArrayList<>();

			do {
				types.add(nextParametrizedReferenceType());
			} while (advanceIfHasNext('&'));

		} else {
			types = Collections.emptyList();
		}

		return GenericDeclarationType.of(name, types);
	}


	/**
	 * @return Следующий список аргументов метода.
	 * Каждый аргумент запрашивается у переданной функции
	 */
	public <T extends Type> Stream<T> nextMethodArguments(Supplier<? extends T> nextTypeFunction) {
		if (requireNext('(').advanceIfHasNext(')')) {
			return Stream.empty();
		}

		Stream<T> stream = nextTypesStream(nextTypeFunction);
		requireNext(')');
		return stream;
	}

	/**
	 * @return Следующий список аргументов метода
	 */
	public Stream<Type> nextMethodArguments() {
		return nextMethodArguments(this::nextType);
	}

	/**
	 * @return Следующий список аргументов метода, которые могут быть параметризованы
	 */
	public Stream<Type> nextParametrizedMethodArguments() {
		return nextMethodArguments(this::nextParametrizedType);
	}


	/**
	 * @return Следующий дескриптор метода, включающий в себя возвращаемый тип и аргументы
	 */
	private String nextMethodDescriptor() {
		Type returnType = nextType();
		return nextMethodArguments().map(Type::getEncodedName)
				.collect(Collectors.joining("", "(", ")")) + returnType.getEncodedName();
	}


	public <T extends Type> Stream<T> nextTypesStream(Supplier<? extends T> nextTypeFunction) {
		return Stream.concat(
				Stream.of(nextTypeFunction.get()),
				Stream.generate(() -> advanceIfHasNext(',') ? nextTypeFunction.get() : null).takeWhile(Objects::nonNull)
		);
	}

	public Stream<Type> nextTypesStream() {
		return nextTypesStream(this::nextType);
	}

	public Stream<RealReferenceType> nextParametrizedReferenceTypesStream() {
		return nextTypesStream(this::nextParametrizedReferenceType);
	}

	public Stream<ReferenceType> nextParameterTypesStream() {
		return nextTypesStream(this::nextParameterType);
	}

	public Stream<ClassType> nextClassTypesStream() {
		return nextTypesStream().map(type -> castOrThrowParseException(type, ClassType.class));
	}

	public Stream<GenericDeclarationType> nextGenericDeclarationTypesStream() {
		return nextTypesStream(this::nextGenericDeclarationType);
	}


	@SuppressWarnings("unchecked")
	private static <T extends Type> T castOrThrowParseException(Type type, Class<T> clazz) {
		if (clazz.isInstance(type))
			return (T)type;

		throw new ParseException("unexpected type " + type);
	}


	/** @return Следующее число как {@code byte} */
	public int nextByte() {
		return nextSignedNumber("byte", number -> (byte)number == number);
	}

	/** @return Следующее число как {@code short} */
	public int nextShort() {
		return nextSignedNumber("short", number -> (short)number == number);
	}


	/** @return Следующее число как {@code int} */
	public int nextInt() {
		return nextSignedNumber("int", number -> true);
	}

	private int nextSignedNumber(String expected, IntPredicate predicate) {
		String str = nextIntegerNumberString(expected);

		if (parseNumber(str) instanceof Integer integer) {
			int value = integer;

			if (predicate.test(value)) {
				return value;
			}

			throw new ParseException("value " + value + " is too large for " + expected);
		}

		throw ParseException.expectedButGot(expected, str);
	}


	/** @return Следующее беззнаковое число как {@code byte} */
	public int nextUnsignedByte() {
		return nextUnsignedNumber("unsigned byte", number -> (number & 0xFF) == number);
	}

	/** @return Следующее беззнаковое число как {@code short} */
	public int nextUnsignedShort() {
		return nextUnsignedNumber("unsigned short", number -> (number & 0xFFFF) == number);
	}

	/** @return Следующее беззнаковое число как {@code int} */
	public int nextUnsignedInt() {
		return nextUnsignedNumber("unsigned int", number -> true);
	}


	private int nextUnsignedNumber(String expected, IntPredicate predicate) {
		String str = nextIntegerNumberString(expected);

		if (parseNumber(str) instanceof Integer integer) {
			int value = integer;

			if (value >= 0) {
				if (predicate.test(value)) {
					return value;
				}

				throw new ParseException("value " + value + " is too large for " + expected);
			}
		}

		throw ParseException.expectedButGot(expected, str);
	}


	public long nextLong() {
		return nextNumber("long", Long.class);
	}

	public float nextFloat() {
		return nextNumber("float", Float.class);
	}

	public double nextDouble() {
		return nextNumber("double", Double.class);
	}

	private <N extends Number> N nextNumber(String expected, Class<N> clazz) {
		String str = nextIntegerNumberString(expected);

		Number num = parseNumber(str);

		if (clazz.isInstance(num)) {
			return clazz.cast(num);
		}

		throw ParseException.expectedButGot(expected, str);
	}


	private String nextIntegerNumberString(String expected) {
		return nextString(CharPredicate.INTEGRAL_NUMBER, NonMatchingCharHandler.throwingParseException(expected));
	}

	/**
	 * @return Индекс следующей константы в переданном пуле констант.
	 * Константа может быть Integer, Long, Float, Double или String
	 */
	public int nextLiteralConstant(ConstantPool pool) {
		String str = tryReadStringLiteral();

		if (str != null) {
			return pool.findOrAddString(str);
		}

		int ch = tryReadCharLiteral();

		if (ch != EOF_CHAR) {
			return pool.findOrAddInteger(ch);
		}

		if (advanceIfHasNext(TRUE)) {
			return pool.findOrAddInteger(1);
		}

		if (advanceIfHasNext(FALSE)) {
			return pool.findOrAddInteger(0);
		}

		return pool.findOrAddNumber(parseNumber(nextString(
				CharPredicate.ANY_NUMBER,
				NonMatchingCharHandler.throwingParseException("constant")
		)));
	}

	/**
	 * @return Индекс следующей константы в переданном пуле констант
	 */
	public int nextConstant(ConstantPool pool) {
		String str = tryReadStringLiteral();

		if (str != null) {
			return pool.findOrAddString(str);
		}

		int ch = tryReadCharLiteral();

		if (ch != EOF_CHAR) {
			return pool.findOrAddInteger(ch);
		}

		if (advanceIfHasNext(TRUE)) {
			return pool.findOrAddInteger(1);
		}

		if (advanceIfHasNext(FALSE)) {
			return pool.findOrAddInteger(0);
		}

		if (advanceIfHasNext('#')) {
			String constantName = nextName();

			requireNext('(');

			int index = switch (constantName) {
				case Constant.UTF8    -> pool.findOrAddUtf8(nextStringLiteral());
				case Constant.INTEGER -> pool.findOrAddInteger(nextInt());
				case Constant.FLOAT   -> pool.findOrAddFloat(nextFloat());
				case Constant.LONG    -> pool.findOrAddLong(nextLong());
				case Constant.DOUBLE  -> pool.findOrAddDouble(nextDouble());
				case Constant.CLASS   -> pool.classIndexFor(nextClassType());
				case Constant.STRING  -> pool.findOrAddString(nextStringLiteral());

				case Constant.FIELDREF            -> nextFieldref(pool);
				case Constant.METHODREF           -> nextMethodref(pool);
				case Constant.INTERFACE_METHODREF -> nextInterfaceMethodref(pool);

				case Constant.NAME_AND_TYPE ->
					pool.findOrAddNameAndType(
							pool.findOrAddUtf8(nextStringLiteral()),
							pool.findOrAddUtf8(requireNext(',').nextStringLiteral())
					);

				case Constant.METHOD_HANDLE ->
					pool.findOrAddMethodHandle(
							ReferenceKind.byName(nextName()),
							requireNext(',').nextReferenceConstant(pool)
					);

				case Constant.METHOD_TYPE ->
					pool.findOrAddMethodType(
							pool.findOrAddUtf8(nextMethodDescriptor())
					);

				case Constant.INVOKE_DYNAMIC ->
					pool.findOrAddInvokeDynamic(
							nextUnsignedInt(),
							requireNext(',').nextNameAndTypeConstant(pool)
					);

				case Constant.MODULE -> pool.findOrAddModule(nextStringLiteral());
				case Constant.PACKAGE -> pool.findOrAddPackage(nextStringLiteral());

				default -> throw new ParseException("Illegal constant name \"" + constantName + "\"");
			};

			requireNext(')');
			return index;
		}

		return pool.findOrAddNumber(parseNumber(nextString(
				CharPredicate.ANY_NUMBER,
				NonMatchingCharHandler.throwingParseException("constant")
		)));
	}

	/**
	 * @return Индекс следующей константы в переданном пуле констант
	 * @throws ParseException если тип константы не соответствует переданному типу
	 */
	public int nextConstant(ConstantPool pool, Class<? extends Constant> constType) {
		int index = nextConstant(pool);

		Constant constant = pool.get(index);

		if (constType.isInstance(constant)) {
			return index;
		}

		throw ParseException.expectedButGot(
				constType.getSimpleName(),
				constant.getClass().getSimpleName(),
				""
		);
	}

	public int nextReferenceConstant(ConstantPool pool) {
		return nextConstant(pool, ReferenceConstant.class);
	}

	public int nextNameAndTypeConstant(ConstantPool pool) {
		return nextConstant(pool, NameAndTypeConstant.class);
	}

	/**
	 * @return Следующую {@link x590.yava.constpool.FieldrefConstant},
	 * описанную дескриптором поля
	 */
	public int nextFieldref(ConstantPool pool) {
		Type type = nextType();

		var classAndName = nextClassAndName();

		return pool.findOrAddFieldref(
				pool.findOrAddClass(pool.findOrAddUtf8(classAndName.first())),
				pool.findOrAddNameAndType(
						pool.findOrAddUtf8(classAndName.second()),
						pool.findOrAddUtf8(type.getEncodedName())
				)
		);
	}

	/**
	 * @return Следующую {@link x590.yava.constpool.MethodrefConstant},
	 * описанную дескриптором метода
	 */
	public int nextMethodref(ConstantPool pool) {
		return nextMethodref(pool, pool::findOrAddMethodref);
	}

	/**
	 * @return Следующую {@link x590.yava.constpool.InterfaceMethodrefConstant},
	 * описанную дескриптором метода
	 */
	public int nextInterfaceMethodref(ConstantPool pool) {
		return nextMethodref(pool, pool::findOrAddInterfaceMethodref);
	}

	private int nextMethodref(ConstantPool pool, IntBinaryOperator methodrefGetter) {
		Type returnType = nextType();

		var classAndName = nextClassAndName();

		return methodrefGetter.applyAsInt(
				pool.findOrAddClass(pool.findOrAddUtf8(classAndName.first())),
				pool.findOrAddNameAndType(
						pool.findOrAddUtf8(classAndName.second()),
						pool.findOrAddUtf8(
								nextMethodArguments().map(Type::getEncodedName).collect(Collectors.joining("", "(", ")"))
										+ returnType.getEncodedName())
				)
		);
	}

	/**
	 * @return Следующий класс и имя поля или метода.
	 * Имя класса в формате "java/lang/Object"
	 */
	public Pair<String, String> nextClassAndName() {
		String str = nextString(CharPredicate.TYPE, NonMatchingCharHandler.throwingParseException("class and name"));

		String className, name;

		if (str.endsWith(".") && (name = tryReadStringLiteral()) != null) {
			className = str.substring(0, str.length() - 1);

		} else {
			int pointIndex = str.lastIndexOf('.');

			if (pointIndex == -1) {
				throw ParseException.expectedButGot("class and name", str);
			}

			className = str.substring(0, pointIndex);
			name = str.substring(pointIndex + 1);
		}

		return Pair.of(className.replace('.', '/'), name);
	}

	/**
	 * @return Следующую строку, если она есть, иначе {@code null}
	 */
	public @Nullable String tryReadStringLiteral() {
		if (advanceIfHasNext('"')) {
			StringBuilder value = new StringBuilder();

			for (int ch = read(); ; value.append((char)ch), ch = read()) {
				if (ch == '"') {
					return value.toString();
				} else {
					ch = nextLiteralChar(ch);
				}
			}
		}

		return null;
	}

	/**
	 * @return Следующий литерал символа, если он есть, иначе {@link #EOF_CHAR}
	 */
	public int tryReadCharLiteral() {
		if (advanceIfHasNext('\'')) {
			int ch = nextLiteralChar(read());
			require('\'');
			return ch;
		}

		return EOF_CHAR;
	}

	private int nextLiteralChar(int ch) {
		if (ch != '\\') {
			if (ch == '\n') {
				throw new ParseException("Literal is not closed before end of line");
			}

			return ch;
		}

		int escaped = read();

		return switch (escaped) {
			case '"', '\'', '\\' -> escaped;
			case 'n' -> '\n';
			case 'r' -> '\r';
			case 'f' -> '\f';
			case 'u' -> readHexDigit() << 24 | readHexDigit() << 16 | readHexDigit() << 8 | readHexDigit();

			default -> {
				int readChars = 0;
				int code = 0;

				for (int digit = escaped; readChars < 3; readChars++, digit = read()) {

					if (digit >= '0' && digit <= '7') {
						code = (code << 3) | (digit - '0');

						if (code > 0xFF) {
							code >>= 3;
							buffer(digit);
							break;
						}

					} else {
						buffer(digit);
						break;
					}
				}

				if (readChars > 0) {
					yield code;
				}

				throw new ParseException("invalid char escaping \"\\" + (char) escaped + "\"");
			}

			case EOF_CHAR -> throw ParseException.expectedButGotEof("char");
		};
	}

	private int readHexDigit() {
		int ch = read();

		if (ch == EOF_CHAR) {
			throw ParseException.expectedButGotEof("hex digit");
		}

		if (ch >= '0' && ch <= '9') {
			return ch - '0';
		}

		if (ch >= 'a' && ch <= 'f') {
			return ch - 'a';
		}

		if (ch >= 'A' && ch <= 'F') {
			return ch - 'A';
		}

		throw ParseException.expectedButGot("hex digit", String.valueOf((char) ch));
	}


	public String nextStringLiteral() {
		String literal = tryReadStringLiteral();

		if (literal != null) {
			return literal;
		}

		throw ParseException.expectedButGot("string literal", previewString());
	}

	public Number nextNumber() {
		return parseNumber(nextString(
				CharPredicate.ANY_NUMBER,
				NonMatchingCharHandler.throwingParseException("number")
		));
	}

	private static Number parseNumber(String str) {

		try {
			char firstChar = str.charAt(0);
			int start = firstChar == '-' || firstChar == '+' ? 1 : 0;

			int length = str.length();
			int factualLength = (int) str.chars().skip(start).takeWhile(Character::isDigit).count();


			int radix =
					factualLength > 1 && str.charAt(start) == '0' ?
							factualLength > 2 ?
									switch (str.charAt(start + 1)) {
										case 'x' -> 16;
										case 'b' -> 2;
										default -> 8;
									} :
									8 :
							10;

			char lowerLastChar = Character.toLowerCase(str.charAt(length - 1));

			int cutEnd = 0;

			ObjIntBooleanFunction<String, Number> parser = switch (lowerLastChar) {
				case 'l' -> {
					cutEnd = 1;
					yield LongUtil::parseLong;
				}

				case 'f' -> {
					if (radix != 16 || str.toLowerCase().contains("p")) {
						cutEnd = 1;
						yield FloatUtil::parseFloat;
					}

					yield IntegerUtil::parseInt;
				}

				case 'd' -> {
					if (radix != 16 || str.toLowerCase().contains("p")) {
						cutEnd = 1;
						yield DoubleUtil::parseDouble;
					}

					yield IntegerUtil::parseInt;
				}

				default -> str.contains(".") || str.toLowerCase().contains(FPUtil.getExponentSeparatorString(radix)) ?
						DoubleUtil::parseDouble :
						IntegerUtil::parseInt;
			};

			return parser.apply(
					str.substring(
							start + (radix == 8 ? 1 : radix != 10 ? 2 : 0),
							length - cutEnd
					),
					radix, firstChar == '-'
			);


		} catch (NumberFormatException ex) {
			throw ParseException.expectedButGot("constant", str).initCause(ex);
		}
	}


	private String nextString(CharPredicate predicate, NonMatchingCharHandler handler) {

		int ch = next();

		if (ch == EOF_CHAR) {
			return handler.handleEof();
		}

		if (!predicate.isStartChar(ch)) {
			return handler.handleNonMatchingChar((char)ch);
		}

		StringBuilder str = new StringBuilder();

		do {
			str.append((char)ch);
			ch = read();
		} while (predicate.isPartChar(ch));

		buffer(ch);

		return str.toString();
	}


	public int preview() {
		int ch = next();
		buffer(ch);
		return ch;
	}

	public String previewString() {
		String str = nextString();
		buffer(str);
		return str;
	}

	public boolean advanceIfHasNext(String str) {
		String next = nextString(CharPredicate.withLength(str.length()), NonMatchingCharHandler.DEFAULT_HANDLER);

		if (next.equals(str)) {
			return true;
		} else {
			buffer(next);
			return false;
		}
	}

	public boolean advanceIfHasNext(char ch) {
		int next = next();

		if (next == ch) {
			return true;
		} else {
			buffer(next);
			return false;
		}
	}

	public AssemblingInputStream require(char expected) {
		return require(expected, read());
	}

	/**
	 * @throws ParseException если достигнут конец файла или следующий символ не совпадает с требуемым
	 */
	public AssemblingInputStream requireNext(char expected) {
		return require(expected, next());
	}

	private AssemblingInputStream require(char expected, int actual) {
		if (actual != expected) {
			throw actual == EOF_CHAR ?
					ParseException.expectedButGotEof(expected) :
					ParseException.expectedButGot(expected, (char)actual);
		}

		return this;
	}

	public AssemblingInputStream requireNext(String expected) {
		return requireNextString(expected);
	}

	public AssemblingInputStream requireNextString(String expected) {
		String str = nextString();
		if (!str.equals(expected)) {
			throw str.isEmpty() ?
					ParseException.expectedButGotEof(expected, "\"") :
					ParseException.expectedButGot(expected, str, "\"", "\"");
		}

		return this;
	}
}
