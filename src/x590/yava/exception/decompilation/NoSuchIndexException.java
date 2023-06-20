package x590.yava.exception.decompilation;

import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.io.Serial;

public class NoSuchIndexException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -3643704245365695435L;

	public NoSuchIndexException() {
		super();
	}

	public NoSuchIndexException(String message) {
		super(message);
	}

	public NoSuchIndexException(int index, Int2IntMap table) {
		super("index " + index + " is not found in table " + table);
	}
}
