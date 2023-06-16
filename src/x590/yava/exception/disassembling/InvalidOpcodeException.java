package x590.yava.exception.disassembling;

import java.io.Serial;

public class InvalidOpcodeException extends ClassFormatException {

	@Serial
	private static final long serialVersionUID = 4282824108768458025L;
	
	public InvalidOpcodeException() {
		super();
	}
	
	public InvalidOpcodeException(String message) {
		super(message);
	}
	
	public InvalidOpcodeException(Throwable cause) {
		super(cause);
	}
	
	public InvalidOpcodeException(String message, Throwable cause) {
		super(message, cause);
	}
}
