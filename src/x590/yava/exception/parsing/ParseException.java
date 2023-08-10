package x590.yava.exception.parsing;

import java.io.Serial;

public class ParseException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -1811782000015693429L;

	private static final String END_OF_FILE = "end of file";


	public ParseException() {
		super();
	}

	public ParseException(String message) {
		super(message);
	}

	/**
	 * @return Новый экземпляр {@link ParseException} с сообщением, которое содержит
	 * {@code expected} без кавычек и {@code actual}, окружённое кавычками "
	 */
	public static ParseException expectedButGot(String expected, String actual) {
		return expectedButGot(expected, actual, "\"");
	}

	/**
	 * @return Новый экземпляр {@link ParseException} с сообщением, которое содержит
	 * {@code expected} без кавычек и {@code actual}, окружённое кавычками {@code quote}
	 */
	public static ParseException expectedButGot(String expected, String actual, String quote) {
		return new ParseException("expected " + expected + ", got " + quote + actual + quote);
	}

	/**
	 * @return Новый экземпляр {@link ParseException} с сообщением, которое содержит
	 * {@code expected}, окружённое кавычками {@code expectedQuote} и
	 * {@code actual}, окружённое кавычками {@code actualQuote}
	 */
	public static ParseException expectedButGot(String expected, String actual, String expectedQuote, String actualQuote) {
		return new ParseException("expected " + expectedQuote + expected + expectedQuote
				+ ", got " + actualQuote + actual + actualQuote);
	}


	/**
	 * @return Новый экземпляр {@link ParseException} с сообщением, которое содержит
	 * {@code expected} и {@code actual}, окружённые кавычками '
	 */
	public static ParseException expectedButGot(char expected, char actual) {
		return expectedButGot(expected, actual, "'");
	}

	/**
	 * @return Новый экземпляр {@link ParseException} с сообщением, которое содержит
	 * {@code expected} и {@code actual}, окружённые кавычками {@code quote}
	 */
	public static ParseException expectedButGot(char expected, char actual, String quote) {
		return new ParseException("expected " + quote + expected + quote + ", got " + quote + actual + quote);
	}

	/**
	 * @return Новый экземпляр {@link ParseException} с сообщением о конце файла,
	 * которое содержит {@code expected} без кавычек
	 */
	public static ParseException expectedButGotEof(String expected) {
		return expectedButGotEof(expected, "");
	}

	/**
	 * @return Новый экземпляр {@link ParseException} с сообщением о конце файла,
	 * которое содержит {@code expected}, окружённые кавычками {@code quote}
	 */
	public static ParseException expectedButGotEof(String expected, String quote) {
		return new ParseException("expected " + quote + expected + quote + ", got " + END_OF_FILE);
	}

	/**
	 * @return Новый экземпляр {@link ParseException} с сообщением о конце файла,
	 * которое содержит {@code expected}, окружённые кавычками '
	 */
	public static ParseException expectedButGotEof(char expected) {
		return new ParseException("expected '" + expected + "', got " + END_OF_FILE);
	}


	public ParseException(Throwable cause) {
		super(cause);
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseException initCause(Throwable cause) {
		return (ParseException) super.initCause(cause);
	}
}
