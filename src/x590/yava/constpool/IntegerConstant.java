package x590.yava.constpool;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.field.FieldDescriptor;
import x590.yava.field.JavaField;
import x590.yava.io.ExtendedDataOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.IConstOperation;
import x590.yava.type.Type;
import x590.yava.type.UncertainIntegralType;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;
import x590.yava.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import static x590.yava.type.primitive.PrimitiveType.*;

public final class IntegerConstant extends ConstableValueConstant<Integer> {

	private final int value;

	private final Type type;

	private static Type getTypeFor(int value) {
		if ((value & 0x1) == value)
			return BYTE_SHORT_INT_CHAR_BOOLEAN;

		int minCapacity =
				(byte) value == value ? BYTE_CAPACITY :
						(short) value == value ? SHORT_CAPACITY : INT_CAPACITY;

		return UncertainIntegralType.getInstance(minCapacity, INT_CAPACITY,
				UncertainIntegralType.includeCharIf((char) value == value));
	}

	IntegerConstant(int value) {
		this.value = value;
		this.type = getTypeFor(value);
	}

	public int getValue() {
		return value;
	}

	@Override
	public Integer getValueAsObject() {
		return value;
	}

	@Override
	public int intValue() {
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
	public Type getType() {
		return type;
	}

	@Override
	protected Type getWidestType() {
		return INT;
	}

	@Override
	public String getConstantName() {
		return "Integer";
	}

	@Override
	public Operation toOperation() {
		return new IConstOperation(this);
	}


	private final Map<Type, JavaField> constantFields = new HashMap<>(5, 1F);

	@Override
	protected JavaField findConstantField(ClassInfo classinfo, Type type) {
		return constantFields.computeIfAbsent(type, tp -> super.findConstantField(classinfo, tp));
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		if (canUseConstants() &&
				// -Integer.MIN_VALUE == Integer.MIN_VALUE, поэтому проверки на -Integer.MIN_VALUE здесь нет
				(value == Integer.MAX_VALUE ||
						value == Integer.MIN_VALUE ||
						value == -Integer.MAX_VALUE)) {

			classinfo.addImport(ClassType.INTEGER);
		}
	}


	private static final FieldDescriptor
			MAX_VALUE_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.INT, ClassType.INTEGER, "MAX_VALUE"),
			MIN_VALUE_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.INT, ClassType.INTEGER, "MIN_VALUE");

	private boolean writeConstantIfEquals(StringifyOutputStream out, ClassInfo classinfo, @Nullable FieldDescriptor ownerConstant,
										  int value, FieldDescriptor requiredConstant) {

		return writeConstantIfEquals(out, classinfo, ownerConstant, this.value == value, this.value == -value, requiredConstant);
	}

	@Override
	public void writeValue(StringifyOutputStream out, ClassInfo classinfo, Type type, int flags, @Nullable FieldDescriptor ownerConstant) {

		if (!canUseConstants() ||
				!writeConstantIfEquals(out, classinfo, ownerConstant, Integer.MAX_VALUE, MAX_VALUE_DESCRIPTOR) &&
						!writeConstantIfEquals(out, classinfo, ownerConstant, Integer.MIN_VALUE, MIN_VALUE_DESCRIPTOR)
		) {
			out.write(
					type.canCastToNarrowest(BOOLEAN) ? StringUtil.toLiteral(value != 0) :
					type.canCastToNarrowest(BYTE)    ? StringUtil.toLiteral((byte) value, flags) :
					type.canCastToNarrowest(CHAR)    ? StringUtil.toLiteral((char) value, flags) :
					type.canCastToNarrowest(SHORT)   ? StringUtil.toLiteral((short) value, flags) :
													   StringUtil.toLiteral(value, flags)
			);
		}
	}

	@Override
	public void serialize(ExtendedDataOutputStream out) {
		out.writeByte(TAG_INTEGER);
		out.writeInt(value);
	}

	@Override
	protected boolean canUseConstant(JavaField constant) {
		return constant.getConstantValue().intValue() == value;
	}

	@Override
	public boolean isOne() {
		return value == 1;
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof IntegerConstant constant && this.equals(constant);
	}

	public boolean equals(IntegerConstant other) {
		return this == other || this.value == other.value;
	}

	@Override
	public String toString() {
		return "IntegerConstant {" + value + "}";
	}
}
