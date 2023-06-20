package x590.yava.exception.disassembling;

import x590.yava.io.ExtendedStringInputStream;

import java.io.Serial;

public class InvalidMethodDescriptorException extends NameDisassemblingException {

	@Serial
	private static final long serialVersionUID = -3425457614642349478L;

	public InvalidMethodDescriptorException(String encodedName) {
		super(encodedName);
	}

	public InvalidMethodDescriptorException(String encodedName, int pos) {
		super(encodedName, pos);
	}

	public InvalidMethodDescriptorException(ExtendedStringInputStream in) {
		super(in);
	}

	public InvalidMethodDescriptorException(ExtendedStringInputStream in, int pos) {
		super(in, pos);
	}
}
