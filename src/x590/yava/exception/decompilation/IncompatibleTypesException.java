package x590.yava.exception.decompilation;

import x590.yava.type.ICastingKind;
import x590.yava.type.Type;

import java.io.Serial;

public class IncompatibleTypesException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = -4152058291198634873L;

	public IncompatibleTypesException() {
		super();
	}

	public IncompatibleTypesException(String message) {
		super(message);
	}

	public IncompatibleTypesException(Type type1, Type type2, ICastingKind kind) {
		super("Incompatible types in " + kind.lowerCaseName() + " conversation: " +
				type1.toString() + " (" + type1.getClass().getSimpleName() + ") and " +
				type2.toString() + " (" + type2.getClass().getSimpleName() + ')');
	}
}
