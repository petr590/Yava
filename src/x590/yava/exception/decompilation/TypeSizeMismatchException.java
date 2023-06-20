package x590.yava.exception.decompilation;

import x590.yava.type.Type;
import x590.yava.type.TypeSize;

import java.io.Serial;

public class TypeSizeMismatchException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = 1L;

	public TypeSizeMismatchException() {
		super();
	}

	public TypeSizeMismatchException(String message) {
		super(message);
	}

	public TypeSizeMismatchException(Throwable cause) {
		super(cause);
	}

	public TypeSizeMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public TypeSizeMismatchException(TypeSize requiredSize, TypeSize size, Type type) {
		super("Required " + requiredSize + ", got " + size + " from type " + type);
	}
}
