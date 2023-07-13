package x590.yava.io;

import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import x590.util.*;
import x590.util.annotation.Nullable;
import x590.util.function.ObjIntBooleanFunction;
import x590.util.io.UncheckedInputStream;
import x590.yava.Keywords;
import x590.yava.attribute.code.CodeAttribute;
import x590.yava.constpool.ConstantPool;
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

	public int nextLabel(CodeAttribute.LabelsManager labelsManager) {
		String name = nextString();

		if (labelsManager.hasLabel(name)) {
			return labelsManager.getLabelPos(name) - labelsManager.getInstructionPos();
		}

		labelsManager.addForwardingEntry(name);

		return 0;
	}


	private record CharPredicate(IntPredicate startPredicate, IntPredicate partPredicate) {

		public static final CharPredicate
				STRING = new CharPredicate(Character::isJavaIdentifierPart, Character::isJavaIdentifierPart),
				NAME = new CharPredicate(Character::isJavaIdentifierStart, Character::isJavaIdentifierPart),
				TYPE = new CharPredicate(CharPredicate::isJavaTypeStart, CharPredicate::isJavaTypePart),
				GENERIC_DECLARATION_TYPE = STRING,
				INTEGRAL_NUMBER = new CharPredicate(CharPredicate::isIntegralNumberStart, CharPredicate::isIntegralNumberPart),
				ANY_NUMBER = new CharPredicate(CharPredicate::isAnyNumberStart, CharPredicate::isAnyNumberPart);

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

	private record NonMatchingCharHandler(
			Char2ObjectFunction<String> nonMatchingCharHandler,
			Supplier<String> eofGetter
	) {

		private static final Map<String, NonMatchingCharHandler> THROWING_HANDLERS = new HashMap<>();

		public static final NonMatchingCharHandler DEFAULT_HANDLER = new NonMatchingCharHandler(String::valueOf, () -> "");

		public static NonMatchingCharHandler throwingParseException(String expected) {
			return THROWING_HANDLERS.computeIfAbsent(expected,
					(String exp) -> new NonMatchingCharHandler(
							actual -> {
								throw ParseException.expectedButGot(exp, String.valueOf(actual));
							},
							() -> {
								throw ParseException.expectedButGot(exp, ParseException.END_OF_FILE);
							}
					)
			);
		}

		public String handleNonMatchingChar(char ch) {
			return nonMatchingCharHandler.get(ch);
		}

		public String getEof() {
			return eofGetter.get();
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
	 * @return Следующую строку, которая содержит либо название, либо другой непробельный символ.
	 */
	public String nextString() {
		return nextString(CharPredicate.STRING, NonMatchingCharHandler.DEFAULT_HANDLER);
	}

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
		String name = nextString(CharPredicate.TYPE, NonMatchingCharHandler.throwingParseException("type name"));

		int nestingLevel = 0;

		while (advanceIfHasNext('[') && advanceIfHasNext(']')) {
			nestingLevel++;
		}

		Type type = switch (name) {
			case BYTE -> PrimitiveType.BYTE;
			case SHORT -> PrimitiveType.SHORT;
			case CHAR -> PrimitiveType.CHAR;
			case INT -> PrimitiveType.INT;
			case LONG -> PrimitiveType.LONG;
			case FLOAT -> PrimitiveType.FLOAT;
			case DOUBLE -> PrimitiveType.DOUBLE;
			case BOOLEAN -> PrimitiveType.BOOLEAN;
			case VOID -> PrimitiveType.VOID;
			default -> {
				if (Keywords.isKeyword(name)) {
					throw ParseException.expectedButGot("type", name);
				}

				yield ClassType.fromDescriptor(name.replace('.', '/'));
			}
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

	public RealReferenceType nextParametrizedReferenceType() {
		return castOrThrowParseException(nextParametrizedType(), RealReferenceType.class);
	}

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


	public <T extends Type> Stream<T> nextMethodArguments(Supplier<? extends T> nextTypeFunction) {
		if (requireNext('(').advanceIfHasNext(')')) {
			return Stream.empty();
		}

		Stream<T> stream = nextTypesStream(nextTypeFunction);
		requireNext(')');
		return stream;
	}

	public Stream<Type> nextMethodArguments() {
		return nextMethodArguments(this::nextType);
	}

	public Stream<Type> nextParametrizedMethodArguments() {
		return nextMethodArguments(this::nextParametrizedType);
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

	public int nextConstant(ConstantPool pool) {
		String str = tryReadStringLiteral();

		if (str != null) {
			return pool.findOrAddString(str);
		}

//		if(Character.isDigit(preview())) {
		return pool.findOrAddNumber(parseNumber(nextString(
				CharPredicate.ANY_NUMBER,
				NonMatchingCharHandler.throwingParseException("constant")
		)));
//		}
	}

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

	public int nextMethodref(ConstantPool pool) {
		Type returnType = nextType();

		var classAndName = nextClassAndName();

		return pool.findOrAddMethodref(
				pool.findOrAddClass(pool.findOrAddUtf8(classAndName.first())),
				pool.findOrAddNameAndType(
						pool.findOrAddUtf8(classAndName.second()),
						pool.findOrAddUtf8(
								nextMethodArguments().map(Type::getEncodedName).collect(Collectors.joining("", "(", ")"))
										+ returnType.getEncodedName())
				)
		);
	}

	private Pair<String, String> nextClassAndName() {
		String str = nextString(CharPredicate.TYPE, NonMatchingCharHandler.throwingParseException("method"));

		String className, name;

		if (str.endsWith(".") && (name = tryReadStringLiteral()) != null) {
			className = str.substring(0, str.length() - 1);

		} else {
			int pointIndex = str.lastIndexOf('.');

			if (pointIndex == -1) {
				throw ParseException.expectedButGot("method", str);
			}

			className = str.substring(0, pointIndex);
			name = str.substring(pointIndex + 1);
		}

		return Pair.of(className.replace('.', '/'), name);
	}

	private @Nullable String tryReadStringLiteral() {
		if (advanceIfHasNext('"')) {
			StringBuilder value = new StringBuilder();

			for (int ch = read(); ; value.append((char)ch), ch = read()) {
				if (ch == '\\') {
					int escaped = read();

					ch = switch (escaped) {
						case '"' -> '"';
						case '\'' -> '\'';
						case '\\' -> '\\';
						case 'n' -> '\n';
						case 'r' -> '\r';
						case 'f' -> '\f';
						default -> throw new ParseException("invalid char escaping \"\\" + escaped + "\"");
					};

				} else if (ch == '"') {
					break;
				}
			}

			return value.toString();
		}

		return null;
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
			int factualLength = length - start;


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
			return handler.getEof();
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

	public AssemblingInputStream requireNext(char expected) {
		int ch = next();
		if (ch != expected) {
			throw ch == EOF_CHAR ?
					newUncheckedEOFException() :
					ParseException.expectedButGot(expected, (char)ch);
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
					newUncheckedEOFException() :
					ParseException.expectedButGot(expected, str);
		}

		return this;
	}
}
