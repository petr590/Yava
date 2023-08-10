package x590.yava.attribute;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.constvalue.ConstableValueConstant;
import x590.yava.constpool.ConstantPool;
import x590.yava.field.FieldDescriptor;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.type.Type;
import x590.yava.util.StringUtil;

public final class ConstantValueAttribute extends Attribute {

	public final int valueIndex;
	public final ConstableValueConstant<?> value;

	ConstantValueAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length, 2);
		this.valueIndex = in.readUnsignedShort();
		this.value = pool.get(valueIndex);
	}

	ConstantValueAttribute(String name, AssemblingInputStream in, ConstantPool pool) {
		super(name, 2);
		this.valueIndex = in.requireNext(':').nextLiteralConstant(pool);
		this.value = pool.get(valueIndex);
		in.requireNext(';');
	}

	public void writeTo(StringifyOutputStream out, ClassInfo classinfo, Type type, @Nullable FieldDescriptor descriptor) {
		value.writeValue(out, classinfo, type, StringUtil.IMPLICIT, descriptor);
	}

	@Override
	public void serializeData(AssemblingOutputStream out, ConstantPool pool) {
		out.writeShort(valueIndex);
	}
}
