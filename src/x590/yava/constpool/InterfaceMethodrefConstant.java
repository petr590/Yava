package x590.yava.constpool;

import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;

public final class InterfaceMethodrefConstant extends MethodrefConstant {

	InterfaceMethodrefConstant(ExtendedDataInputStream in) {
		super(in);
	}

	public InterfaceMethodrefConstant(int classIndex, int nameAndTypeIndex, ConstantPool pool) {
		super(classIndex, nameAndTypeIndex, pool);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out .recordByte(TAG_INTERFACE_METHODREF)
			.recordShort(getClassIndex())
			.recordShort(getNameAndTypeIndex());
	}
}
