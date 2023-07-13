package x590.yava.attribute;

import x590.yava.constpool.ConstantPool;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;

public final class UnknownAttribute extends Attribute {

	private final byte[] data;

	UnknownAttribute(String name, int length, ExtendedDataInputStream in) {
		super(name, length);
		this.data = new byte[length];
		in.readFully(data);
	}

	@Override
	public void serializeData(AssemblingOutputStream out, ConstantPool pool) {
		out.writeByteArray(data);
	}
}
