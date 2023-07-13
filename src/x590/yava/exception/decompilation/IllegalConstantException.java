package x590.yava.exception.decompilation;

import java.io.Serial;

public class IllegalConstantException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = 2695138715731964812L;

	public IllegalConstantException() {
		super();
	}

	public IllegalConstantException(String message) {
		super(message);
	}

	public IllegalConstantException(Throwable cause) {
		super(cause);
	}

	public IllegalConstantException(String message, Throwable cause) {
		super(message, cause);
	}
}
