package x590.yava.exception.disassembling;

import x590.yava.io.ExtendedStringInputStream;

import java.io.Serial;

public class NameDisassemblingException extends DisassemblingException {

	@Serial
	private static final long serialVersionUID = -5848599648448714466L;
	
	public NameDisassemblingException(String encodedName) {
		super('"' + encodedName + '"');
	}
	
	public NameDisassemblingException(String encodedName, int pos) {
		super('"' + encodedName + '"' + " (at pos " + pos + ")");
	}
	
	public NameDisassemblingException(ExtendedStringInputStream in) {
		super('"' + in.getAllFromMark() + '"');
	}
	
	public NameDisassemblingException(ExtendedStringInputStream in, int pos) {
		super('"' + in.getAllFromMark() + '"' + " (at pos " + pos + ")");
	}
}
