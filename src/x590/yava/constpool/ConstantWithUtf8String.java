package x590.yava.constpool;

import x590.yava.io.ExtendedDataInputStream;

abstract class ConstantWithUtf8String extends Constant {

	final int valueIndex;
	String value;

	ConstantWithUtf8String(ExtendedDataInputStream in) {
		this.valueIndex = in.readUnsignedShort();
	}

	ConstantWithUtf8String(int valueIndex, ConstantPool pool) {
		this.valueIndex = valueIndex;
		init(pool);
	}

	@Override
	protected void init(ConstantPool pool) {
		this.value = pool.getUtf8String(valueIndex);
	}

	public String getString() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("%sConstant { %s }", getConstantName(), value);
	}
}
