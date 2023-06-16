package x590.yava.attribute;

import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;

public final class UnknownAttribute extends Attribute {
	
	private final byte[] data;
	
	protected UnknownAttribute(String name, int length, ExtendedDataInputStream in) {
		super(name, length);
		this.data = new byte[length];
		in.readFully(data);
	}
	
	@Override
	public void serialize(ExtendedDataOutputStream out) {
		serializeHeader(out);
		out.write(data);
	}
}
