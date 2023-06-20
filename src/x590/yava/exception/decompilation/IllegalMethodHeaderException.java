package x590.yava.exception.decompilation;

import java.io.Serial;

public class IllegalMethodHeaderException extends IllegalHeaderException {

	@Serial
	private static final long serialVersionUID = 4849479055648724003L;

	public IllegalMethodHeaderException() {
		super();
	}

	public IllegalMethodHeaderException(String message) {
		super(message);
	}
}
