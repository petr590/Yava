package x590.yava.exception.decompilation;

import java.io.Serial;

public class IllegalClassHeaderException extends IllegalHeaderException {

	@Serial
	private static final long serialVersionUID = -1506886376951024578L;
	
	public IllegalClassHeaderException() {
		super();
	}
	
	public IllegalClassHeaderException(String message) {
		super(message);
	}
}
