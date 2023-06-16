package x590.yava.attribute;

import x590.yava.constpool.ConstantPool;
import x590.yava.io.ExtendedDataInputStream;

public class SourceFileAttribute extends Attribute {
	
	private final String sourceFileName;
	
	SourceFileAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);
		checkLength(name, length, 2);
		this.sourceFileName = pool.getUtf8String(in.readUnsignedShort());
	}
	
	public String getSourceFileName() {
		return sourceFileName;
	}
}
