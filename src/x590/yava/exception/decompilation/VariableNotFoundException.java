package x590.yava.exception.decompilation;

import java.io.Serial;

public class VariableNotFoundException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = 7564801037667031273L;

	public VariableNotFoundException() {
		super();
	}

	public VariableNotFoundException(String message) {
		super(message);
	}
}
