package x590.yava.exception.disassembling;

import x590.yava.io.ExtendedStringInputStream;

import java.io.Serial;

public class InvalidArrayNameException extends InvalidTypeNameException {

	@Serial
	private static final long serialVersionUID = -2261099827704297685L;

	public InvalidArrayNameException(String encodedName) {
		super(encodedName);
	}

	public InvalidArrayNameException(String encodedName, int pos) {
		super(encodedName, pos);
	}

	public InvalidArrayNameException(ExtendedStringInputStream in) {
		super(in);
	}

	public InvalidArrayNameException(ExtendedStringInputStream in, int pos) {
		super(in, pos);
	}
}
