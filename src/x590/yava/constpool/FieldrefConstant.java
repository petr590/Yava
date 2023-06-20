package x590.yava.constpool;

import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;

public final class FieldrefConstant extends ReferenceConstant {

	protected FieldrefConstant(ExtendedDataInputStream in) {
		super(in);
	}

	public FieldrefConstant(int classIndex, int nameAndTypeIndex, ConstantPool pool) {
		super(classIndex, nameAndTypeIndex, pool);
	}

	@Override
	public String getConstantName() {
		return "Fieldref";
	}

	@Override
	public void serialize(ExtendedDataOutputStream out) {
		out.writeByte(TAG_FIELDREF);
		out.writeShort(getClassIndex());
		out.writeShort(getNameAndTypeIndex());
	}
}
