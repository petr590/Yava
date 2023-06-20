package x590.yava.constpool;

import x590.yava.clazz.ClassInfo;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.writable.StringifyWritable;

public final class ModuleConstant extends ConstantWithUtf8String implements StringifyWritable<ClassInfo> {

	public ModuleConstant(ExtendedDataInputStream in) {
		super(in);
	}

	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.write(value);
	}

	@Override
	public String getConstantName() {
		return "Module";
	}

	@Override
	public void serialize(ExtendedDataOutputStream out) {
		out.write(TAG_MODULE);
		out.write(valueIndex);
	}

	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof ModuleConstant moduleConstant && this.equals(moduleConstant);
	}

	public boolean equals(ModuleConstant other) {
		return value.equals(other.value);
	}
}
