package x590.yava.constpool;

import x590.yava.clazz.ClassInfo;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.writable.StringifyWritable;

public final class PackageConstant extends ConstantWithUtf8String implements StringifyWritable<ClassInfo> {

	public PackageConstant(ExtendedDataInputStream in) {
		super(in);
	}

	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.write(value.replace('/', '.'));
	}

	@Override
	public String getConstantName() {
		return "Package";
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out.recordByte(TAG_PACKAGE).writeShort(valueIndex);
	}

	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof PackageConstant packageConstant && this.equals(packageConstant);
	}

	public boolean equals(PackageConstant other) {
		return value.equals(other.value);
	}
}
