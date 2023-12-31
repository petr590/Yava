package x590.yava.constpool;

import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.constvalue.ConstValueConstant;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.MethodTypeConstOperation;
import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;

public final class MethodTypeConstant extends ConstValueConstant {

	private final int descriptorIndex;
	private Utf8Constant descriptor;

	MethodTypeConstant(ExtendedDataInputStream in) {
		descriptorIndex = in.readUnsignedShort();
	}

	MethodTypeConstant(int descriptorIndex, ConstantPool pool) {
		this.descriptorIndex = descriptorIndex;
		init(pool);
	}

	@Override
	protected void init(ConstantPool pool) {
		descriptor = pool.get(descriptorIndex);
	}

	public int getDescriptorIndex() {
		return descriptorIndex;
	}

	public Utf8Constant getDescriptor() {
		return descriptor;
	}

	@Override
	public Type getType() {
		return ClassType.METHOD_TYPE;
	}

	@Override
	public String getConstantName() {
		return METHOD_TYPE;
	}

	@Override
	public Operation toOperation() {
		return new MethodTypeConstOperation(this);
	}

	@Override
	public String toString() {
		return String.format("MethodTypeConstant { %s }", descriptor);
	}

	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.write("#MethodType#");
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo, Type type, int flags) {
		out.print("#MethodType(").print(descriptor.getString()).print(")");
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out.recordByte(TAG_METHOD_TYPE).writeByte(descriptorIndex);
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof MethodTypeConstant constant && this.equals(constant);
	}

	public boolean equals(MethodTypeConstant other) {
		return this == other || this.descriptor.equals(other.descriptor);
	}
}
