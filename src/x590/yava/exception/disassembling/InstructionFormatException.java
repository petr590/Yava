package x590.yava.exception.disassembling;

import java.io.Serial;

public class InstructionFormatException extends ClassFormatException {

	@Serial
	private static final long serialVersionUID = -1765290011748352973L;

	public InstructionFormatException() {
		super();
	}

	public InstructionFormatException(String message) {
		super(message);
	}

	public InstructionFormatException(Throwable cause) {
		super(cause);
	}

	public InstructionFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
