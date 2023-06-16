package x590.yava.exception.disassembling;

import x590.yava.io.ExtendedStringInputStream;

import java.io.Serial;

public class InvalidClassNameException extends InvalidTypeNameException {

	@Serial
	private static final long serialVersionUID = -4562573452176773150L;
	
	public InvalidClassNameException(String encodedName) {
		super(encodedName);
	}
	
	public InvalidClassNameException(String encodedName, int pos) {
		super(encodedName, pos);
	}
	
	public InvalidClassNameException(ExtendedStringInputStream in) {
		super(in);
	}
	
	public InvalidClassNameException(ExtendedStringInputStream in, int pos) {
		super(in, pos);
	}
}
