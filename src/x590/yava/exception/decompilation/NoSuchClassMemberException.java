package x590.yava.exception.decompilation;

import x590.yava.Descriptor;

import java.io.Serial;

public abstract class NoSuchClassMemberException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = 814234522525746027L;

	public NoSuchClassMemberException() {
		super();
	}

	public NoSuchClassMemberException(String message) {
		super(message);
	}

	public NoSuchClassMemberException(Descriptor<?> descriptor) {
		super(descriptor.toString());
	}
}
