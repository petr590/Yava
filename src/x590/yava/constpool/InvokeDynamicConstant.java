package x590.yava.constpool;

import x590.yava.attribute.AttributeType;
import x590.yava.attribute.Attributes;
import x590.yava.attribute.BootstrapMethodsAttribute.BootstrapMethod;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;

public final class InvokeDynamicConstant extends Constant {

	private final int bootstrapMethodIndex;
	private final int nameAndTypeIndex;
	private NameAndTypeConstant nameAndType;

	InvokeDynamicConstant(ExtendedDataInputStream in) {
		bootstrapMethodIndex = in.readUnsignedShort();
		nameAndTypeIndex = in.readUnsignedShort();
	}

	InvokeDynamicConstant(int bootstrapMethodIndex, int nameAndTypeIndex, ConstantPool pool) {
		this.bootstrapMethodIndex = bootstrapMethodIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
		init(pool);
	}

	@Override
	protected void init(ConstantPool pool) {
		nameAndType = pool.get(nameAndTypeIndex);
	}

	public int getBootstrapMethodIndex() {
		return bootstrapMethodIndex;
	}

	public BootstrapMethod getBootstrapMethod(Attributes attributes) {
		return attributes.get(AttributeType.BOOTSTRAP_METHODS).getBootstrapMethod(bootstrapMethodIndex);
	}

	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}

	public NameAndTypeConstant getNameAndType() {
		return nameAndType;
	}

	@Override
	public String getConstantName() {
		return INVOKE_DYNAMIC;
	}

	@Override
	public String toString() {
		return String.format("MethodTypeConstant { bootstrapMethodIndex = %d, nameAndType = %s }", bootstrapMethodIndex, nameAndType);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out .recordByte(TAG_INVOKE_DYNAMIC)
			.recordShort(bootstrapMethodIndex)
			.recordShort(nameAndTypeIndex);
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof InvokeDynamicConstant constant && this.equals(constant);
	}

	public boolean equals(InvokeDynamicConstant other) {
		return this == other || this.nameAndType.equals(other.nameAndType);
	}
}
