package x590.yava.util;

import x590.util.IntegerUtil;
import x590.util.LongUtil;
import x590.util.MathUtil;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.main.Config;
import x590.yava.main.Config.UsagePolicy;
import x590.yava.main.Yava;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringUtil {

	public static String toLowerCamelCase(String str) {
		int length = str.length();

		StringBuilder result = new StringBuilder(length);

		int i = 0;
		for (; i < length; i++) {
			char c = str.charAt(i);

			if (c >= 'A' && c <= 'Z')
				result.append(Character.toLowerCase(c));
			else
				break;
		}

		while (i < length)
			result.append(str.charAt(i++));

		return result.toString();
	}

	public static String toTitleCase(String name) {
		return name.isEmpty() ?
				"" :
				Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}


	private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

	private static String encodeUtf8(int c) {
		if (c < 0) {
			throw new IllegalArgumentException("Char code U+" + IntegerUtil.hex(c) + " is too large for encode");
		}

		// 0xxxxxxx
		if (c < 0x80) return String.valueOf((char)c);
		// 110xxxxx 10xxxxxx
		if (c < 0x800)
			return new String(new byte[] { (byte)((c >> 6 & 0x1F) | 0xC0), (byte)((c       & 0x3F) | 0x80)}, UTF8_CHARSET);
		// 1110xxxx 10xxxxxx 10xxxxxx
		if (c < 0x10000)
			return new String(new byte[] { (byte)((c >> 12 & 0xF) | 0xE0), (byte)((c >>  6 & 0x3F) | 0x80), (byte)((c       & 0x3F) | 0x80) }, UTF8_CHARSET);
		// 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
		if (c < 0x200000)
			return new String(new byte[] { (byte)((c >> 18 & 0x7) | 0xF0), (byte)((c >> 12 & 0x3F) | 0x80), (byte)((c >> 6 & 0x3F) | 0x80), (byte)((c       & 0x3F) | 0x80) }, UTF8_CHARSET);
		// 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
		if (c < 0x4000000)
			return new String(new byte[] { (byte)((c >> 24 & 0x3) | 0xF8), (byte)((c >> 18 & 0x3F) | 0x80), (byte)((c >> 12 & 0x3F) | 0x80), (byte)((c >>  6 & 0x3F) | 0x80), (byte)((c      & 0x3F) | 0x80) }, UTF8_CHARSET);

		// 1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
		return     new String(new byte[] { (byte)((c >> 30 & 0x1) | 0xFC), (byte)((c >> 24 & 0x3F) | 0x80), (byte)((c >> 18 & 0x3F) | 0x80), (byte)((c >> 12 & 0x3F) | 0x80), (byte)((c >> 6 & 0x3F) | 0x80), (byte)((c & 0x3F) | 0x80) }, UTF8_CHARSET);
	}


	// surrogate pairs in UTF-16: 0xD800-0xDFFF
	private static String escapeUtf16(int ch) {
		assert ch <= 0x10FFFF;

		if (ch > 0xFFFF) {
			ch -= 0x10000;
			return "\\u" + IntegerUtil.hex4(((ch >> 10) & 0x3FF) | 0xD800) + "\\u" + IntegerUtil.hex4((ch & 0x3FF) | 0xDC00);
		}

		return "\\u" + IntegerUtil.hex4(ch);
	}

	private static String escapeUtf16Octal(int ch) {
		assert ch <= 0x10FFFF;

		if (ch > 0xFFFF) {
			ch -= 0x10000;
			return "\\" + Integer.toOctalString(((ch >> 10) & 0x3FF) | 0xD800) + "\\" + Integer.toOctalString((ch & 0x3FF) | 0xDC00);
		}

		return "\\" + Integer.toOctalString(ch);
	}

	private static boolean isNotDisplayedChar(int ch) {
		return ch < 0x20 || (ch >= 0x7F && ch < 0xA0) // Control characters
				|| (ch >= 0xFFEF && ch < 0x10000)         // Unknown characters
				|| (ch >= 0xD800 && ch < 0xE000);         // Surrogate pairs in UTF-16
	}


	private static String charToString(char quote, int ch) {
		assert quote == '"' || quote == '\'' : "Invalid quote";

		return switch (ch) {
			case '\b' -> "\\b";
			case '\t' -> "\\t";
			case '\n' -> "\\n";
			case '\f' -> "\\f";
			case '\r' -> "\\r";
			case '\\' -> "\\\\";
			default -> ch == quote ? "\\" + quote :
					isNotDisplayedChar(ch) || (CONFIG.escapeUnicodeChars() && ch >= 0x80) ?
							quote == '\'' && ch < 0x100 ?
									escapeUtf16Octal(ch) :
									escapeUtf16(ch) :
							encodeUtf8(ch);
		};
	}

	public static String stringToLiteral(String str) {
		byte[] bytes = str.getBytes();

		StringBuilder result = new StringBuilder(bytes.length).append('"');

		for (int i = 0, length = bytes.length; i < length; ++i) {
			int ch = bytes[i] & 0xFF;

			if ((ch & 0xE0) == 0xC0) {
				i++;

				if (i >= length)
					throw new DecompilationException("Unexpected end of the string: " + i + " >= " + length);

				if ((bytes[i] & 0xC0) != 0x80)
					throw new DecompilationException("Invalid string encoding");

				ch = (ch & 0x1F) << 6 | (bytes[i] & 0x3F);

			} else if ((ch & 0xF0) == 0xE0) {

				if (ch == 0xED && i + 5 < length &&
						(bytes[i + 1] & 0xF0) == 0xA0 && (bytes[i + 2] & 0xC0) == 0x80 && (bytes[i + 3] & 0xFF) == 0xED
						&& (bytes[i + 4] & 0xF0) == 0xB0 && (bytes[i + 5] & 0xC0) == 0x80) {

					result.append(encodeUtf8(0x10000 | (bytes[++i] & 0xF) << 16 |
							(bytes[++i] & 0x3F) << 10 | (bytes[i += 2] & 0xF) << 6 | (bytes[++i] & 0x3F)));

					continue;
				}

				if (i + 2 >= length)
					throw new DecompilationException("Unexpected end of the string: " + i + " + " + 2 + " >= " + length);

				if ((bytes[i + 1] & 0xC0) != 0x80 || (bytes[i + 2] & 0xC0) != 0x80)
					throw new DecompilationException("Invalid string encoding");

				ch = (ch & 0xF) << 12 | (bytes[++i] & 0x3F) << 6 | (bytes[++i] & 0x3F);
			}

			result.append(charToString('"', ch));
		}

		return result.append('"').toString();
	}


	private static final Config CONFIG = Yava.getConfig();


	private static String numberConstantToString(int value, int flags) {
		UsagePolicy hexNumbersUsagePolicy = CONFIG.hexNumbersUsagePolicy();

		if (hexNumbersUsagePolicy != UsagePolicy.NEVER && (flags & USE_HEX) != 0 ||
				hexNumbersUsagePolicy == UsagePolicy.ALWAYS ||
				hexNumbersUsagePolicy == UsagePolicy.AUTO && (value >= 16 || value <= -16) &&
						(MathUtil.isPowerOfTwo(+value) || MathUtil.isPowerOfTwo(+value + 1) ||
								MathUtil.isPowerOfTwo(-value) || MathUtil.isPowerOfTwo(-value + 1))
		) {
			return value < 0 && (flags & SIGNED) != 0 ?
					'-' + IntegerUtil.hexWithPrefix(-value) :
					IntegerUtil.hexWithPrefix(value);
		}

		return Integer.toString(value);
	}

	private static String numberConstantToString(long value, int flags) {
		UsagePolicy hexNumbersUsagePolicy = CONFIG.hexNumbersUsagePolicy();

		if (hexNumbersUsagePolicy != UsagePolicy.NEVER && (flags & USE_HEX) != 0 ||
				hexNumbersUsagePolicy == UsagePolicy.ALWAYS ||
				hexNumbersUsagePolicy == UsagePolicy.AUTO && (value >= 16 || value <= -16) &&
						(MathUtil.isPowerOfTwo(+value) || MathUtil.isPowerOfTwo(+value + 1) ||
								MathUtil.isPowerOfTwo(-value) || MathUtil.isPowerOfTwo(-value + 1))
		) {
			return value < 0 && (flags & SIGNED) != 0 ?
					'-' + LongUtil.hexWithPrefix(-value) :
					LongUtil.hexWithPrefix(value);
		}

		return Long.toString(value);
	}


	public static final int
			NONE     = 0x0,
			USE_HEX  = 0x1,
			SIGNED   = 0x2,
			IMPLICIT = 0x4;


	public static String booleanToLiteral(boolean value) {
		return Boolean.toString(value);
	}

	public static String byteToLiteral(byte value, int flags) {
		return numberConstantToString(value, flags);
	}

	public static String shortToLiteral(short value, int flags) {
		return numberConstantToString(value, flags);
	}

	public static String charToLiteral(char value) {
		return "'" + charToString('\'', value) + "'";
	}

	public static String intToLiteral(int value, int flags) {
		return numberConstantToString(value, flags);
	}

	public static String longToLiteral(long value, int flags) {
		return numberConstantToString(value, flags) + CONFIG.getLongSuffix();
	}

	public static String floatToLiteral(float value) {
		if (!Float.isFinite(value)) {

			String end = (CONFIG.printTrailingZero() ? ".0" : "") + CONFIG.getFloatSuffix();

			return  value == Float.POSITIVE_INFINITY ?  "1" + end + " / 0" + end :
					value == Float.NEGATIVE_INFINITY ? "-1" + end + " / 0" + end :
														"0" + end + " / 0" + end;
		}

		return (!CONFIG.printTrailingZero() && (int)value == value ? Integer.toString((int) value) : Float.toString(value)) +
				CONFIG.getFloatSuffix();
	}

	public static String doubleToLiteral(double value) {
		if (!Double.isFinite(value)) {

			String end = CONFIG.printTrailingZero() ?
					CONFIG.printDoubleSuffix() ? ".0" + CONFIG.getDoubleSuffix() : ".0" :
					Character.toString(CONFIG.getDoubleSuffix());

			return  value == Double.POSITIVE_INFINITY ?  "1" + end + " / 0" + end :
					value == Double.NEGATIVE_INFINITY ? "-1" + end + " / 0" + end :
														 "0" + end + " / 0" + end;
		}

		return !CONFIG.printTrailingZero() && (int)value == value ?
				Integer.toString((int)value) + CONFIG.getDoubleSuffix() :
				CONFIG.printDoubleSuffix() ?
						Double.toString(value) + CONFIG.getDoubleSuffix() :
						Double.toString(value);
	}
}
