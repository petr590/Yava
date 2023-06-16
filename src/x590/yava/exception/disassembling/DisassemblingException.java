package x590.yava.exception.disassembling;

import java.io.Serial;

public class DisassemblingException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -1857134960093430464L;
	
	public DisassemblingException() {
		super();
	}
	
	public DisassemblingException(String message) {
		super(message);
	}
	
	public DisassemblingException(Throwable cause) {
		super(cause);
	}
	
	public DisassemblingException(String message, Throwable cause) {
		super(message, cause);
	}
}
