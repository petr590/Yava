package x590.yava.constpool;

import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;

public final class NameAndTypeConstant extends Constant {

	private final int nameIndex, descriptorIndex;
	private Utf8Constant name, descriptor;

	public NameAndTypeConstant(ExtendedDataInputStream in) {
		this.nameIndex = in.readUnsignedShort();
		this.descriptorIndex = in.readUnsignedShort();
	}

	public NameAndTypeConstant(int nameIndex, int descriptorIndex, ConstantPool pool) {
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		init(pool);
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public int getDescriptorIndex() {
		return descriptorIndex;
	}

	@Override
	protected void init(ConstantPool pool) {
		name = pool.get(nameIndex);
		descriptor = pool.get(descriptorIndex);
	}

	public Utf8Constant getNameConstant() {
		return name;
	}

	public Utf8Constant getDescriptor() {
		return descriptor;
	}

	@Override
	public String getConstantName() {
		return NAME_AND_TYPE;
	}

	@Override
	public String toString() {
		return String.format("NameAndTypeConstant { name = %s, descriptor = %s }", name, descriptor);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out .recordByte(TAG_NAME_AND_TYPE)
			.recordShort(nameIndex)
			.recordShort(descriptorIndex);
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof NameAndTypeConstant constant && this.equals(constant);
	}

	public boolean equals(NameAndTypeConstant other) {
		return this == other || this.name.equals(other.name) && this.descriptor.equals(other.descriptor);
	}
}
