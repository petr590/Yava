package x590.jdecompiler.exception;

import x590.jdecompiler.io.ExtendedStringReader;

public class InvalidMethodDescriptorException extends NameDisassemblingException {
	
	private static final long serialVersionUID = -3425457614642349478L;
	
	public InvalidMethodDescriptorException(String encodedName) {
		super(encodedName);
	}
	
	public InvalidMethodDescriptorException(String encodedName, int pos) {
		super(encodedName, pos);
	}
	
	public InvalidMethodDescriptorException(ExtendedStringReader in) {
		super(in);
	}
	
	public InvalidMethodDescriptorException(ExtendedStringReader in, int pos) {
		super(in, pos);
	}
}