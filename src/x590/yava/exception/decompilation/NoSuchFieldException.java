package x590.yava.exception.decompilation;

import x590.yava.field.FieldDescriptor;

import java.io.Serial;

public class NoSuchFieldException extends NoSuchClassMemberException {

	@Serial
	private static final long serialVersionUID = 3224048661190569290L;
	
	public NoSuchFieldException(String message) {
		super(message);
	}
	
	public NoSuchFieldException(FieldDescriptor descriptor) {
		super(descriptor);
	}
}
