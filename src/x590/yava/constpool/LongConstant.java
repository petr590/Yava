package x590.yava.constpool;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.field.FieldDescriptor;
import x590.yava.field.JavaField;
import x590.yava.io.ExtendedDataOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.LConstOperation;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;
import x590.yava.util.StringUtil;

public final class LongConstant extends SingleConstableValueConstant<Long> {

	private final long value;

	LongConstant(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	@Override
	public Long getValueAsObject() {
		return value;
	}

	@Override
	public long longValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public boolean holdsTwo() {
		return true;
	}

	@Override
	public Type getType() {
		return PrimitiveType.LONG;
	}

	@Override
	public String getConstantName() {
		return "Long";
	}

	@Override
	public Operation toOperation() {
		return new LConstOperation(this);
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		if (canUseConstants() && // -Long.MIN_VALUE == Long.MIN_VALUE, поэтому проверки на -Long.MIN_VALUE здесь нет
				(value == Long.MAX_VALUE ||
						value == Long.MIN_VALUE ||
						value == -Long.MAX_VALUE)) {
			classinfo.addImport(ClassType.LONG);
		}
	}


	private static final FieldDescriptor
			MAX_VALUE_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.LONG, ClassType.LONG, "MAX_VALUE"),
			MIN_VALUE_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.LONG, ClassType.LONG, "MIN_VALUE");

	private boolean writeConstantIfEquals(StringifyOutputStream out, ClassInfo classinfo, @Nullable FieldDescriptor ownerConstant,
										  long value, FieldDescriptor requiredConstant) {

		return writeConstantIfEquals(out, classinfo, ownerConstant, this.value == value, this.value == -value, requiredConstant);
	}

	@Override
	public void writeValue(StringifyOutputStream out, ClassInfo classinfo, Type type, int flags, @Nullable FieldDescriptor ownerConstant) {

		if (!canUseConstants() ||
				!writeConstantIfEquals(out, classinfo, ownerConstant, Long.MAX_VALUE, MAX_VALUE_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Long.MIN_VALUE, MIN_VALUE_DESCRIPTOR)
		) {
			out.write((flags & StringUtil.IMPLICIT) != 0 && canImplicitCastToInt() ?
					StringUtil.toLiteral((int)value, flags) :
					StringUtil.toLiteral(value, flags));
		}
	}

	@Override
	public String toString() {
		return String.format("LongConstant { %d }", value);
	}

	@Override
	public void serialize(ExtendedDataOutputStream out) {
		out.writeByte(TAG_LONG);
		out.writeLong(value);
	}

	@Override
	protected boolean canUseConstant(JavaField constant) {
		return constant.getConstantValue().longValue() == value;
	}

	@Override
	public boolean canImplicitCastToInt() {
		return (int)value == value;
	}

	@Override
	public boolean isOne() {
		return value == 1;
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof LongConstant constant && this.equals(constant);
	}

	public boolean equals(LongConstant other) {
		return this == other || value == other.value;
	}
}
