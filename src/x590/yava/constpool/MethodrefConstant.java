package x590.yava.constpool;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.method.MethodDescriptor;

public class MethodrefConstant extends ReferenceConstant {

	private @Nullable MethodDescriptor descriptor;

	protected MethodrefConstant(ExtendedDataInputStream in) {
		super(in);
	}

	MethodrefConstant(int classIndex, int nameAndTypeIndex, ConstantPool pool) {
		super(classIndex, nameAndTypeIndex, pool);
	}

	@Override
	public String getConstantName() {
		return METHODREF;
	}

	public MethodDescriptor toDescriptor() {
		var descriptor = this.descriptor;
		return descriptor != null ? descriptor : (this.descriptor = MethodDescriptor.from(this));
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		toDescriptor().writeAsMethodref(out, classinfo);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out .recordByte(TAG_METHODREF)
			.recordShort(getClassIndex())
			.recordShort(getNameAndTypeIndex());
	}
}
