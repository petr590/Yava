package x590.yava.exception.disassembling;

import java.io.Serial;

public class ClassFormatException extends DisassemblingException {

	@Serial
	private static final long serialVersionUID = 187754517612972423L;

	public ClassFormatException() {
		super();
	}

	public ClassFormatException(String message) {
		super(message);
	}

	public ClassFormatException(Throwable cause) {
		super(cause);
	}

	public ClassFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
