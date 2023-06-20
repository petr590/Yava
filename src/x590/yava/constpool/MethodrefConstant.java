package x590.yava.constpool;

import x590.util.annotation.Nullable;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;
import x590.yava.method.MethodDescriptor;

public class MethodrefConstant extends ReferenceConstant {

	private @Nullable MethodDescriptor descriptor;

	protected MethodrefConstant(ExtendedDataInputStream in) {
		super(in);
	}

	public MethodrefConstant(int classIndex, int nameAndTypeIndex, ConstantPool pool) {
		super(classIndex, nameAndTypeIndex, pool);
	}

	@Override
	public String getConstantName() {
		return "Methodref";
	}

	public MethodDescriptor toDescriptor() {
		var descriptor = this.descriptor;
		return descriptor != null ? descriptor : (this.descriptor = MethodDescriptor.from(this));
	}

	@Override
	public void serialize(ExtendedDataOutputStream out) {
		out.writeByte(TAG_METHODREF);
		out.writeShort(getClassIndex());
		out.writeShort(getNameAndTypeIndex());
	}
}
