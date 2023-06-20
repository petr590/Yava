package x590.yava.exception.decompilation;

import java.io.Serial;

public class NoSuchConstantException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = -4054909876022589550L;

	public NoSuchConstantException() {
		super();
	}

	public NoSuchConstantException(String message) {
		super(message);
	}
}
