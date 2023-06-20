package x590.yava.exception.disassembling;

import x590.yava.io.ExtendedStringInputStream;

import java.io.Serial;

public class InvalidSignatureException extends InvalidTypeNameException {

	@Serial
	private static final long serialVersionUID = -1561793936986160127L;

	public InvalidSignatureException(String encodedName) {
		super(encodedName);
	}

	public InvalidSignatureException(String encodedName, int pos) {
		super(encodedName, pos);
	}

	public InvalidSignatureException(ExtendedStringInputStream in) {
		super(in);
	}

	public InvalidSignatureException(ExtendedStringInputStream in, int pos) {
		super(in, pos);
	}
}
