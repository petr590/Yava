package x590.yava.exception.parsing;

import java.io.Serial;

public class ParseException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -1811782000015693429L;


	public record Actual(String value, String quotes) {
		public static Actual of(String value) {
			return new Actual(value, "\"");
		}
	}

	public static final Actual END_OF_FILE = new Actual("end of file", "");

	
	public ParseException() {
		super();
	}
	
	public ParseException(String message) {
		super(message);
	}

	public static ParseException expectedButGot(String expected, String actual) {
		return expectedButGot(expected, actual, "\"");
	}

	public static ParseException expectedButGot(String expected, Actual actual) {
		return expectedButGot(expected, actual.value, actual.quotes);
	}

	public static ParseException expectedButGot(String expected, String actual, String quote) {
		return new ParseException("expected " + expected + ", got " + quote + actual + quote);
	}

	public static ParseException expectedButGot(char expected, char actual) {
		return expectedButGot(expected, actual, "'");
	}

	public static ParseException expectedButGot(char expected, char actual, String quote) {
		return new ParseException("expected " + quote + expected + quote + ", got " + quote + actual + quote);
	}
	
	public ParseException(Throwable cause) {
		super(cause);
	}
	
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseException initCause(Throwable cause) {
		return (ParseException)super.initCause(cause);
	}
}
