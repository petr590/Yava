package x590.yava.constpool;

import x590.util.annotation.Nullable;
import x590.yava.constpool.constvalue.*;
import x590.yava.constpool.MethodHandleConstant.ReferenceKind;
import x590.yava.exception.decompilation.IllegalConstantException;
import x590.yava.serializable.JavaSerializable;
import x590.yava.exception.decompilation.NoSuchConstantException;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.type.Type;
import x590.yava.type.reference.RealReferenceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ConstantPool implements JavaSerializable {

	private static final Map<String, Utf8Constant> UTF8_CONSTANTS = new HashMap<>();
	private static final Map<String, StringConstant> STRING_CONSTANTS = new HashMap<>();

	private final List<Constant> data;

	private ConstantPool(ExtendedDataInputStream in) {
		var length = in.readUnsignedShort();
		var data = this.data = new ArrayList<>(length);
		data.add(null); // 0-й элемент всегда null

		for (int i = 1; i < length; ) {
			Constant constant = Constant.readConstant(in);
			data.add(constant);

			if (constant.holdsTwo()) {
				data.add(null);
				i += 2;
			} else {
				i += 1;
			}
		}

		for (Constant constant : data) {
			if (constant != null) {
				constant.init(this);
			}
		}
	}

	private ConstantPool() {
		this.data = new ArrayList<>();
		data.add(null); // 0-й элемент всегда null
	}

	public static ConstantPool read(ExtendedDataInputStream in) {
		return new ConstantPool(in);
	}

	public static ConstantPool newInstance() {
		return new ConstantPool();
	}


	public <C extends Constant> C get(int index) {
		if (index != 0)
			return getNullable(index);

		throw new NoSuchConstantException("By index " + index);
	}

	public <C extends Constant> C get(int index, Class<C> constantClass) {
		C constant = get(index);

		if (constantClass.isInstance(constant)) {
			return constant;
		}

		throw new IllegalConstantException("Expected: " + constantClass.getSimpleName()
				+ ", got " + constant.getConstantName());
	}

	@SuppressWarnings("unchecked")
	public <C extends Constant> @Nullable C getNullable(int index) {
		try {
			return (C)data.get(index);
		} catch (IndexOutOfBoundsException ex) {
			throw new NoSuchConstantException("By index " + index);
		}
	}

	public String getUtf8String(int index) {
		return this.<Utf8Constant>get(index).getString();
	}

	public @Nullable String getNullableUtf8String(int index) {
		Utf8Constant utf8Constant = getNullable(index);
		return utf8Constant == null ? null : utf8Constant.getString();
	}

	public ClassConstant getClassConstant(int index) {
		return get(index);
	}

	public @Nullable ClassConstant getNullableClassConstant(int index) {
		return getNullable(index);
	}


	public int add(Constant constant) {
		int index = data.size();
		data.add(constant);

		if(constant.holdsTwo())
			data.add(null);

		return index;
	}

	private int findOrAdd(Predicate<Constant> equalsPredicate, Supplier<Constant> newConstantSupplier) {
		var data = this.data;

		for (int i = 1, size = data.size(); i < size; i++) {
			if (equalsPredicate.test(data.get(i)))
				return i;
		}

		return add(newConstantSupplier.get());
	}

	public int findOrAddInteger(int value) {
		return findOrAdd(
				constant -> constant instanceof IntegerConstant integer && integer.getValue() == value,
				() -> findOrCreateConstant(value)
		);
	}

	public int findOrAddLong(long value) {
		return findOrAdd(
				constant -> constant instanceof LongConstant longConstant && longConstant.getValue() == value,
				() -> findOrCreateConstant(value)
		);
	}

	public int findOrAddFloat(float value) {
		return findOrAdd(
				constant -> constant instanceof FloatConstant floatConstant && floatConstant.equalsTo(value),
				() -> findOrCreateConstant(value)
		);
	}

	public int findOrAddDouble(double value) {
		return findOrAdd(
				constant -> constant instanceof DoubleConstant doubleConstant && doubleConstant.equalsTo(value),
				() -> findOrCreateConstant(value)
		);
	}

	public int findOrAddNumber(Number number) {
		return findOrAdd(
				constant -> constant instanceof ConstableValueConstant<?> constableValueConstant && constableValueConstant.getValueAsObject().equals(number),
				() -> findOrCreateConstant(number));
	}

	public int findOrAddUtf8(String value) {
		return findOrAdd(
				constant -> constant instanceof Utf8Constant utf8 && utf8.getString().equals(value),
				() -> new Utf8Constant(value)
		);
	}

	public int findOrAddString(String value) {
		int utf8Index = findOrAddUtf8(value);

		return findOrAdd(
				constant -> constant instanceof StringConstant string && string.getString().equals(value),
				() -> new StringConstant(utf8Index, get(utf8Index))
		);
	}

	public int findOrAddClass(int nameIndex) {
		return findOrAdd(
				constant -> constant instanceof ClassConstant clazz && clazz.getNameIndex() == nameIndex,
				() -> new ClassConstant(nameIndex, this)
		);
	}

	public int findOrAddNameAndType(int nameIndex, int descriptorIndex) {
		return findOrAdd(
				constant -> constant instanceof NameAndTypeConstant nameAndType &&
						nameAndType.getNameIndex() == nameIndex && nameAndType.getDescriptorIndex() == descriptorIndex,
				() -> new NameAndTypeConstant(nameIndex, descriptorIndex, this)
		);
	}

	private int findOrAddReferenceConstant(int classIndex, int nameAndTypeIndex, Predicate<Constant> predicate, Supplier<Constant> supplier) {
		return findOrAdd(
				constant -> predicate.test(constant) && constant instanceof ReferenceConstant ref &&
						ref.getClassIndex() == classIndex && ref.getNameAndTypeIndex() == nameAndTypeIndex,
				supplier
		);
	}

	public int findOrAddFieldref(int classIndex, int nameAndTypeIndex) {
		return findOrAddReferenceConstant(classIndex, nameAndTypeIndex,
				constant -> constant instanceof FieldrefConstant,
				() -> new FieldrefConstant(classIndex, nameAndTypeIndex, this)
		);
	}

	public int findOrAddMethodref(int classIndex, int nameAndTypeIndex) {
		return findOrAddReferenceConstant(classIndex, nameAndTypeIndex,
				constant -> constant instanceof MethodrefConstant,
				() -> new MethodrefConstant(classIndex, nameAndTypeIndex, this)
		);
	}

	public int findOrAddInterfaceMethodref(int classIndex, int nameAndTypeIndex) {
		return findOrAddReferenceConstant(classIndex, nameAndTypeIndex,
				constant -> constant instanceof InterfaceMethodrefConstant,
				() -> new InterfaceMethodrefConstant(classIndex, nameAndTypeIndex, this)
		);
	}

	public int findOrAddMethodType(int descriptorIndex) {
		return findOrAdd(
				constant -> constant instanceof MethodTypeConstant methodType &&
						methodType.getDescriptorIndex() == descriptorIndex,
				() -> new MethodTypeConstant(descriptorIndex, this)
		);
	}

	public int findOrAddMethodHandle(ReferenceKind referenceKind, int referenceIndex) {
		return findOrAdd(
				constant -> constant instanceof MethodHandleConstant methodHandle &&
						methodHandle.getReferenceKind() == referenceKind &&
						methodHandle.getReferenceIndex() == referenceIndex,
				() -> new MethodHandleConstant(referenceKind, referenceIndex, this)
		);
	}

	public int findOrAddInvokeDynamic(int bootstrapMethodIndex, int nameAndTypeIndex) {
		return findOrAdd(
				constant -> constant instanceof InvokeDynamicConstant invokeDynamic &&
						invokeDynamic.getBootstrapMethodIndex() == bootstrapMethodIndex &&
						invokeDynamic.getNameAndTypeIndex() == nameAndTypeIndex,
				() -> new InvokeDynamicConstant(bootstrapMethodIndex, nameAndTypeIndex, this)
		);
	}

	public int findOrAddModule(String name) {
		return findOrAdd(
				constant -> constant instanceof ModuleConstant module &&
						module.getString().equals(name),
				() -> new ModuleConstant(findOrAddUtf8(name), this)
		);
	}

	public int findOrAddPackage(String name) {
		return findOrAdd(
				constant -> constant instanceof PackageConstant packageConstant &&
						packageConstant.getString().equals(name),
				() -> new PackageConstant(findOrAddUtf8(name), this)
		);
	}

	public int findOrAddConstant(Constant constant) {
		return findOrAdd(
				constant::equals,
				() -> constant
		);
	}

	public static IntegerConstant findOrCreateConstant(int value) {
		return IntegerConstant.of(value);
	}

	public static LongConstant findOrCreateConstant(long value) {
		return LongConstant.of(value);
	}

	public static FloatConstant findOrCreateConstant(float value) {
		return FloatConstant.of(value);
	}

	public static DoubleConstant findOrCreateConstant(double value) {
		return DoubleConstant.of(value);
	}

	public static IntegerConstant findOrCreateConstant(boolean value) {
		return findOrCreateConstant(value ? 1 : 0);
	}

	public static ConstableValueConstant<? extends Number> findOrCreateConstant(Number value) {
		Class<? extends Number> clazz = value.getClass();
		if (clazz == Integer.class) return findOrCreateConstant(value.intValue());
		if (clazz == Long.class)    return findOrCreateConstant(value.longValue());
		if (clazz == Float.class)   return findOrCreateConstant(value.floatValue());
		if (clazz == Double.class)  return findOrCreateConstant(value.doubleValue());
		if (clazz == Byte.class)    return findOrCreateConstant(value.byteValue());
		if (clazz == Short.class)   return findOrCreateConstant(value.shortValue());
		throw new IllegalArgumentException("value " + value + " has illegal type");
	}

	public static Utf8Constant findOrCreateUtf8Constant(String value) {
		return UTF8_CONSTANTS.computeIfAbsent(value, Utf8Constant::new);
	}

	public static StringConstant findOrCreateConstant(String value) {
		return STRING_CONSTANTS.computeIfAbsent(value, string -> new StringConstant(findOrCreateUtf8Constant(string)));
	}

	public int utf8IndexFor(Type type) {
		return findOrAddUtf8(type.getEncodedName());
	}

	public int classIndexFor(RealReferenceType type) {
		return findOrAddClass(findOrAddUtf8(type.getClassEncodedName()));
	}

	public int classIndexForNullable(@Nullable RealReferenceType type) {
		return type == null ? 0 : classIndexFor(type);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		int size = data.size();
		out.writeShort(size);

		for (int i = 1; i < size; i++) {
			Constant constant = data.get(i);
			if (constant != null)
				constant.serialize(out);
		}
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
