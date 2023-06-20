package x590.yava.constpool;

import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;

public final class InterfaceMethodrefConstant extends MethodrefConstant {

	protected InterfaceMethodrefConstant(ExtendedDataInputStream in) {
		super(in);
	}

	public InterfaceMethodrefConstant(int classIndex, int nameAndTypeIndex, ConstantPool pool) {
		super(classIndex, nameAndTypeIndex, pool);
	}

	@Override
	public void serialize(ExtendedDataOutputStream out) {
		out.writeByte(TAG_INTERFACE_METHODREF);
		out.writeShort(getClassIndex());
		out.writeShort(getNameAndTypeIndex());
	}
}
