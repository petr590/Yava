package x590.yava.exception.decompilation;

import x590.yava.method.MethodDescriptor;

import java.io.Serial;

public class NoSuchMethodException extends NoSuchClassMemberException {

	@Serial
	private static final long serialVersionUID = -6567642506652252778L;

	public NoSuchMethodException() {
		super();
	}

	public NoSuchMethodException(String message) {
		super(message);
	}

	public NoSuchMethodException(MethodDescriptor descriptor) {
		super(descriptor);
	}
}
