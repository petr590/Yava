package x590.yava.exception.decompilation;

import java.io.Serial;

public class IllegalHeaderException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = -3086129383485415839L;

	public IllegalHeaderException() {
		super();
	}

	public IllegalHeaderException(String message) {
		super(message);
	}
}
