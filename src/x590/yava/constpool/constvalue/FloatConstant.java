package x590.yava.constpool.constvalue;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.field.FieldDescriptor;
import x590.yava.field.JavaField;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.operation.constant.FConstOperation;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;
import x590.yava.util.StringUtil;

public final class FloatConstant extends SingleConstableValueConstant<Float> {

	private static final Float2ObjectMap<FloatConstant> INSTANCES = new Float2ObjectArrayMap<>();

	private final float value;

	private FloatConstant(float value) {
		this.value = value;
	}

	public static FloatConstant of(float value) {
		return INSTANCES.computeIfAbsent(value, FloatConstant::new);
	}

	public float getValue() {
		return value;
	}

	@Override
	public Float getValueAsObject() {
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
		return PrimitiveType.FLOAT;
	}

	@Override
	public String getConstantName() {
		return FLOAT;
	}

	@Override
	public Operation toOperation() {
		return new FConstOperation(this);
	}


	private boolean valueEquals(float value) {
		return this.value == value || this.value == -value;
	}

	@Override
	public void addImports(ClassInfo classinfo) {
		if (canUseConstants()) {
			if (valueEquals(FLOAT_PI) || valueEquals(FLOAT_E)) {
				classinfo.addImport(FPMath.MATH_CLASS);

			} else if (!Float.isFinite(value) || valueEquals(Float.MAX_VALUE) || valueEquals(Float.MIN_VALUE) || valueEquals(Float.MIN_NORMAL)) {
				classinfo.addImport(ClassType.FLOAT);
			}
		}
	}


	private static final float
			FLOAT_PI = (float) Math.PI,
			FLOAT_E = (float) Math.E;

	private static final FieldDescriptor
			MAX_VALUE_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.FLOAT, ClassType.FLOAT, "MAX_VALUE"),
			MIN_VALUE_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.FLOAT, ClassType.FLOAT, "MIN_VALUE"),
			MIN_NORMAL_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.FLOAT, ClassType.FLOAT, "MIN_NORMAL"),
			POSITIVE_INFINITY_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.FLOAT, ClassType.FLOAT, "POSITIVE_INFINITY"),
			NEGATIVE_INFINITY_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.FLOAT, ClassType.FLOAT, "NEGATIVE_INFINITY"),
			NAN_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.FLOAT, ClassType.FLOAT, "NaN");

	private boolean writeConstantIfEquals(StringifyOutputStream out, ClassInfo classinfo, @Nullable FieldDescriptor ownerConstant,
										  float value, FieldDescriptor requiredConstant) {

		return writeConstantIfEquals(out, classinfo, ownerConstant, value, requiredConstant, true);
	}

	private boolean writeConstantIfEquals(StringifyOutputStream out, ClassInfo classinfo, @Nullable FieldDescriptor ownerConstant,
										  float value, FieldDescriptor requiredConstant, boolean canNegate) {

		return writeConstantIfEquals(out, classinfo, ownerConstant, this.value == value, canNegate && this.value == -value, requiredConstant);
	}

	@Override
	public void writeValue(StringifyOutputStream out, ClassInfo classinfo, Type type, int flags, @Nullable FieldDescriptor ownerConstant) {

		if (!canUseConstants() ||
				!writeConstantIfEquals(out, classinfo, ownerConstant, Float.MAX_VALUE, MAX_VALUE_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Float.MIN_VALUE, MIN_VALUE_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Float.MIN_NORMAL, MIN_NORMAL_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Float.POSITIVE_INFINITY, POSITIVE_INFINITY_DESCRIPTOR, false) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Float.NEGATIVE_INFINITY, NEGATIVE_INFINITY_DESCRIPTOR, false) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Float.isNaN(value), NAN_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, value == FLOAT_PI, value == -FLOAT_PI, FPMath.PI_DESCRIPTOR, "(float)") &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, value == FLOAT_E, value == -FLOAT_E, FPMath.E_DESCRIPTOR, "(float)")
		) {
			out.write((flags & StringUtil.IMPLICIT) != 0 && canImplicitCastToInt() ?
					StringUtil.intToLiteral((int)value, flags) :
					StringUtil.floatToLiteral(value));
		}
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo, Type type, int flags) {
		out.printFloat(value);
	}

	@Override
	public String toString() {
		return String.format("FloatConstant { %f }", value);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		out.printFloat(value);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out.recordByte(TAG_FLOAT).writeFloat(value);
	}

	@Override
	public int getPriority() {
		return !canUseConstants() ? !Float.isFinite(value) ? Priority.DIVISION : Priority.DEFAULT_PRIORITY :
				valueEquals(FLOAT_PI) || valueEquals(FLOAT_E) ? Priority.CAST : Priority.DEFAULT_PRIORITY;
	}

	@Override
	protected boolean canUseConstant(JavaField constant) {
		return Float.compare(constant.getConstantValue().floatValue(), value) == 0;
	}

	@Override
	public boolean canImplicitCastToInt() {
		return (int)value == value;
	}

	@Override
	public boolean isOne() {
		return value == 1;
	}


	public boolean equalsTo(float value) {
		return Float.compare(this.value, value) == 0;
	}

	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof FloatConstant constant && this.equals(constant);
	}

	public boolean equals(FloatConstant other) {
		return this == other || Float.compare(value, other.value) == 0;
	}
}
