package x590.yava.constpool;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.field.FieldDescriptor;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;

public final class FieldrefConstant extends ReferenceConstant {

	private @Nullable FieldDescriptor descriptor;

	FieldrefConstant(ExtendedDataInputStream in) {
		super(in);
	}

	public FieldrefConstant(int classIndex, int nameAndTypeIndex, ConstantPool pool) {
		super(classIndex, nameAndTypeIndex, pool);
	}

	@Override
	public String getConstantName() {
		return FIELDREF;
	}

	public FieldDescriptor toDescriptor() {
		var descriptor = this.descriptor;
		return descriptor != null ? descriptor : (this.descriptor = FieldDescriptor.from(this));
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		toDescriptor().writeAsFieldref(out, classinfo);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out .recordByte(TAG_FIELDREF)
			.recordShort(getClassIndex())
			.recordShort(getNameAndTypeIndex());
	}
}
