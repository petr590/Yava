package x590.yava.exception.parsing;

public class BytecodeParseException extends ParseException {

	public BytecodeParseException(String message) {
		super(message);
	}

	public BytecodeParseException(Throwable cause) {
		super(cause);
	}

	public BytecodeParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public static BytecodeParseException tooLargeValue(int value, String valueName, String instruction) {
		return new BytecodeParseException(valueName + " " + value + " is too large for " + instruction + " instruction");
	}
}
