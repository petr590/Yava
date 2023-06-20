package x590.yava.exception;

import java.io.Serial;

public class WriteException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 4723052897147649748L;

	public WriteException() {
		super();
	}

	public WriteException(String message) {
		super(message);
	}

	public WriteException(Throwable cause) {
		super(cause);
	}

	public WriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
