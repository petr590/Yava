package x590.yava.exception.decompilation;

import x590.yava.JavaClassElement;
import x590.yava.modifiers.Modifiers;

import java.io.Serial;

public class IllegalModifiersException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = 5025033872617565868L;

	public IllegalModifiersException(String message) {
		super(message);
	}

	public IllegalModifiersException(Modifiers modifiers) {
		super(modifiers.toHexWithPrefix());
	}

	public IllegalModifiersException(JavaClassElement element, Modifiers modifiers) {
		super("in " + element.getModifiersTarget() + ": " + modifiers.toHexWithPrefix());
	}

	public IllegalModifiersException(JavaClassElement element, Modifiers modifiers, String message) {
		super("in " + element.getModifiersTarget() + ": " + modifiers.toHexWithPrefix() + ": " + message);
	}
}
