package x590.yava.constpool.constvalue;

import it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.field.FieldDescriptor;
import x590.yava.field.JavaField;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.operation.constant.DConstOperation;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;
import x590.yava.util.StringUtil;

public final class DoubleConstant extends SingleConstableValueConstant<Double> {

	private static final Double2ObjectMap<DoubleConstant> INSTANCES = new Double2ObjectArrayMap<>();

	private final double value;

	private DoubleConstant(double value) {
		this.value = value;
	}

	public static DoubleConstant of(double value) {
		return INSTANCES.computeIfAbsent(value, DoubleConstant::new);
	}

	public double getValue() {
		return value;
	}

	@Override
	public Double getValueAsObject() {
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
		return PrimitiveType.DOUBLE;
	}

	@Override
	public String getConstantName() {
		return DOUBLE;
	}

	@Override
	public Operation toOperation() {
		return new DConstOperation(this);
	}


	private boolean valueEquals(double value) {
		return this.value == value || this.value == -value;
	}

	@Override
	public void addImports(ClassInfo classinfo) {
		if (canUseConstants()) {
			if (valueEquals(Math.PI) || valueEquals(Math.E)) {
				classinfo.addImport(FPMath.MATH_CLASS);

			} else if (!Double.isFinite(value) || valueEquals(Double.MAX_VALUE) || valueEquals(Double.MIN_VALUE) || valueEquals(Double.MIN_NORMAL)) {
				classinfo.addImport(ClassType.DOUBLE);
			}
		}
	}


	private static final FieldDescriptor
			MAX_VALUE_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.DOUBLE, ClassType.DOUBLE, "MAX_VALUE"),
			MIN_VALUE_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.DOUBLE, ClassType.DOUBLE, "MIN_VALUE"),
			MIN_NORMAL_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.DOUBLE, ClassType.DOUBLE, "MIN_NORMAL"),
			POSITIVE_INFINITY_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.DOUBLE, ClassType.DOUBLE, "POSITIVE_INFINITY"),
			NEGATIVE_INFINITY_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.DOUBLE, ClassType.DOUBLE, "NEGATIVE_INFINITY"),
			NAN_DESCRIPTOR = FieldDescriptor.of(PrimitiveType.DOUBLE, ClassType.DOUBLE, "NaN");

	private boolean writeConstantIfEquals(StringifyOutputStream out, ClassInfo classinfo, @Nullable FieldDescriptor ownerConstant,
										  double value, FieldDescriptor requiredConstant) {

		return writeConstantIfEquals(out, classinfo, ownerConstant, value, requiredConstant, true);
	}

	private boolean writeConstantIfEquals(StringifyOutputStream out, ClassInfo classinfo, @Nullable FieldDescriptor ownerConstant,
										  double value, FieldDescriptor requiredConstant, boolean canNegate) {

		return writeConstantIfEquals(out, classinfo, ownerConstant, this.value == value, canNegate && this.value == -value, requiredConstant);
	}

	@Override
	public void writeValue(StringifyOutputStream out, ClassInfo classinfo, Type type, int flags, @Nullable FieldDescriptor ownerConstant) {

		if (!canUseConstants() ||
				!writeConstantIfEquals(out, classinfo, ownerConstant, Double.MAX_VALUE, MAX_VALUE_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Double.MIN_VALUE, MIN_VALUE_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Double.MIN_NORMAL, MIN_NORMAL_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Double.POSITIVE_INFINITY, POSITIVE_INFINITY_DESCRIPTOR, false) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Double.NEGATIVE_INFINITY, NEGATIVE_INFINITY_DESCRIPTOR, false) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Double.isNaN(value), NAN_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Math.PI, FPMath.PI_DESCRIPTOR) &&
				!writeConstantIfEquals(out, classinfo, ownerConstant, Math.E, FPMath.E_DESCRIPTOR)
		) {
			out.write((flags & StringUtil.IMPLICIT) != 0 && canImplicitCastToInt() ?
					StringUtil.intToLiteral((int)value, flags) :
					StringUtil.doubleToLiteral(value));
		}
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo, Type type, int flags) {
		out.printDouble(value);
	}

	@Override
	public String toString() {
		return String.format("DoubleConstant { %f }", value);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		out.printDouble(value);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out.recordByte(TAG_DOUBLE).writeDouble(value);
	}

	@Override
	public int getPriority() {
		return !canUseConstants() && !Double.isFinite(value) ? Priority.DIVISION : Priority.DEFAULT_PRIORITY;
	}

	@Override
	protected boolean canUseConstant(JavaField constant) {
		return Double.compare(constant.getConstantValue().doubleValue(), value) == 0;
	}

	@Override
	public boolean canImplicitCastToInt() {
		return (int)value == value;
	}

	@Override
	public boolean isOne() {
		return value == 1;
	}


	public boolean equalsTo(double value) {
		return Double.compare(this.value, value) == 0;
	}

	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof DoubleConstant constant && this.equals(constant);
	}

	public boolean equals(DoubleConstant other) {
		return this == other || Double.compare(value, other.value) == 0;
	}
}
