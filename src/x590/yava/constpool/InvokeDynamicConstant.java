package x590.yava.constpool;

import x590.yava.attribute.AttributeType;
import x590.yava.attribute.Attributes;
import x590.yava.attribute.BootstrapMethodsAttribute.BootstrapMethod;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;

public final class InvokeDynamicConstant extends Constant {
	
	private final int bootstrapMethodIndex;
	private final int nameAndTypeIndex;
	private NameAndTypeConstant nameAndType;
	
	public InvokeDynamicConstant(ExtendedDataInputStream in) {
		bootstrapMethodIndex = in.readUnsignedShort();
		nameAndTypeIndex = in.readUnsignedShort();
	}
	
	@Override
	protected void init(ConstantPool pool) {
		nameAndType = pool.get(nameAndTypeIndex);
	}
	
	public BootstrapMethod getBootstrapMethod(Attributes attributes) {
		return attributes.get(AttributeType.BOOTSTRAP_METHODS).getBootstrapMethod(bootstrapMethodIndex);
	}
	
	public NameAndTypeConstant getNameAndType() {
		return nameAndType;
	}
	
	@Override
	public String getConstantName() {
		return "MethodType";
	}
	
	@Override
	public String toString() {
		return String.format("MethodTypeConstant { bootstrapMethodIndex = %d, nameAndType = %s }", bootstrapMethodIndex, nameAndType);
	}
	
	@Override
	public void serialize(ExtendedDataOutputStream out) {
		out.writeByte(TAG_INVOKE_DYNAMIC);
		out.writeShort(bootstrapMethodIndex);
		out.writeShort(nameAndTypeIndex);
	}
	
	
	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof InvokeDynamicConstant constant && this.equals(constant);
	}
	
	public boolean equals(InvokeDynamicConstant other) {
		return this == other || this.nameAndType.equals(other.nameAndType);
	}
}