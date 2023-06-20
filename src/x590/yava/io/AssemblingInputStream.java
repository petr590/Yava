package x590.yava.io;

import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import x590.util.*;
import x590.util.annotation.Nullable;
import x590.util.function.ObjIntBooleanFunction;
import x590.util.io.UncheckedInputStream;
import x590.yava.Keywords;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.parsing.ParseException;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
		buffer(String.valueOf((char) ch));
	}

	public AssemblingInputStream(InputStream in) {
		this.in = in;
	}


	private record CharPredicate(IntPredicate startPredicate, IntPredicate partPredicate) {

		public static final CharPredicate
				STRING = new CharPredicate(Character::isJavaIdentifierPart, Character::isJavaIdentifierPart),
				NAME = new CharPredicate(Character::isJavaIdentifierStart, Character::isJavaIdentifierPart),
				TYPE = new CharPredicate(CharPredicate::isJavaTypeStart, CharPredicate::isJavaTypePart),
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

	/**
	 * @return Следующий тип класса
	 */
	public ClassType nextClassType() {
		return castOrThrowParseException(nextType(), ClassType.class);
	}


	public Stream<Type> nextMethodArguments() {
		if (requireNext('(').advanceIfHasNext(')')) {
			return Stream.empty();
		}

		Stream<Type> stream = nextTypesStream();
		requireNext(')');
		return stream;
	}

	public Stream<Type> nextTypesStream() {
		return Stream.concat(
				Stream.of(nextType()),
				Stream.generate(() -> advanceIfHasNext(',') ? nextType() : null).takeWhile(Objects::nonNull)
		);
	}

	public Stream<ClassType> nextClassTypesStream() {
		return nextTypesStream().map(type -> castOrThrowParseException(type, ClassType.class));
	}


	@SuppressWarnings("unchecked")
	private static <T extends Type> T castOrThrowParseException(Type type, Class<T> clazz) {
		if (clazz.isInstance(type))
			return (T) type;

		throw new ParseException("unexpected type " + type);
	}


	/**
	 * @return Следующее число как {@code int}
	 */
	public int nextInt() {
		String str = nextIntegerNumberString("int");

		if (parseNumber(str) instanceof Integer integer) {
			return integer;
		}

		throw ParseException.expectedButGot("int", str);
	}


	/**
	 * @return Следующее беззнаковое число как {@code int}
	 */
	public int nextUnsignedInt() {
		String str = nextIntegerNumberString("unsigned int");

		if (parseNumber(str) instanceof Integer integer) {
			int value = integer;

			if (value >= 0)
				return value;
		}

		throw ParseException.expectedButGot("unsigned int", str);
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

			for (int ch = read(); ; value.append((char) ch), ch = read()) {
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
			return handler.handleNonMatchingChar((char) ch);
		}

		StringBuilder str = new StringBuilder();

		do {
			str.append((char) ch);
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
					ParseException.expectedButGot(expected, (char) ch);
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
