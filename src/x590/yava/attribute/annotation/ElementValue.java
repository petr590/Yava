package x590.yava.attribute.annotation;

import x590.util.IntegerUtil;
import x590.util.Pair;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.Importable;
import x590.yava.Keywords;
import x590.yava.attribute.Sizes;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.Constant;
import x590.yava.constpool.constvalue.*;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.decompilation.IncompatibleConstantTypeException;
import x590.yava.exception.disassembling.DisassemblingException;
import x590.yava.exception.parsing.ParseException;
import x590.yava.io.*;
import x590.yava.serializable.JavaSerializableWithPool;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ArrayType;
import x590.yava.type.reference.ClassType;
import x590.yava.util.StringUtil;
import x590.yava.writable.DisassemblingStringifyWritable;
import x590.yava.writable.SameDisassemblingStringifyWritable;

import java.lang.constant.Constable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class ElementValue implements
		DisassemblingStringifyWritable<ClassInfo>, Importable, JavaSerializableWithPool {


	private static final int
			TAG_BYTE = 'B',
			TAG_SHORT = 'S',
			TAG_CHAR = 'C',
			TAG_INT = 'I',
			TAG_LONG = 'J',
			TAG_FLOAT = 'F',
			TAG_DOUBLE = 'D',
			TAG_BOOLEAN = 'Z',
			TAG_STRING = 's',
			TAG_ENUM = 'e',
			TAG_CLASS = 'c',
			TAG_ANNOTATION = '@',
			TAG_ARRAY = '[';


	public static class ConstElementValue extends ElementValue {

		private final PrimitiveType type;
		private final ConstValueConstant value;

		private ConstElementValue(PrimitiveType type, ExtendedDataInputStream in, ConstantPool pool) {
			this.type = type;
			this.value = pool.get(in.readUnsignedShort());
		}

		private ConstElementValue(PrimitiveType type, ConstValueConstant value) {
			this.type = type;
			this.value = value;

			if (!value.canUseAs(type)) {
				throw new IncompatibleConstantTypeException(value, type);
			}
		}

		private ConstElementValue(byte value) {
			this(PrimitiveType.BYTE, ConstantPool.findOrCreateConstant(value));
		}

		private ConstElementValue(short value) {
			this(PrimitiveType.SHORT, ConstantPool.findOrCreateConstant(value));
		}

		private ConstElementValue(char value) {
			this(PrimitiveType.CHAR, ConstantPool.findOrCreateConstant(value));
		}

		private ConstElementValue(int value) {
			this(PrimitiveType.INT, ConstantPool.findOrCreateConstant(value));
		}

		private ConstElementValue(long value) {
			this(PrimitiveType.LONG, ConstantPool.findOrCreateConstant(value));
		}

		private ConstElementValue(float value) {
			this(PrimitiveType.FLOAT, ConstantPool.findOrCreateConstant(value));
		}

		private ConstElementValue(double value) {
			this(PrimitiveType.DOUBLE, ConstantPool.findOrCreateConstant(value));
		}

		private ConstElementValue(boolean value) {
			this(PrimitiveType.BOOLEAN, ConstantPool.findOrCreateConstant(value));
		}

		public ConstValueConstant getConstant() {
			return value;
		}

		@Override
		protected int getDataLength() {
			return Sizes.CONSTPOOL_INDEX;
		}

		@Override
		protected int getTag() {
			assert type != PrimitiveType.VOID;
			return type.getEncodedName().charAt(0);
		}

		@Override
		protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
			out.writeShort(pool.findOrAddConstant(value));
		}


		@Override
		public void addImports(ClassInfo classinfo) {
			value.addImports(classinfo);
		}

		@Override
		public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
			value.writeTo(out, classinfo, type, StringUtil.IMPLICIT);
		}

		@Override
		public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
			value.writeDisassembled(out, classinfo, type, StringUtil.IMPLICIT);
		}


		@Override
		public boolean equals(Object other) {
			return this == other || other instanceof ConstElementValue elementValue && this.equals(elementValue);
		}

		public boolean equals(ConstElementValue other) {
			return this == other || type.equals(other.type) && value.equals(other.value);
		}
	}


	public static class StringElementValue extends ElementValue implements SameDisassemblingStringifyWritable<ClassInfo> {

		private final String value;

		private StringElementValue(ExtendedDataInputStream in, ConstantPool pool) {
			this.value = pool.getUtf8String(in.readUnsignedShort());
		}

		private StringElementValue(String value) {
			this.value = value;
		}

		public String getString() {
			return value;
		}

		@Override
		protected int getDataLength() {
			return Sizes.CONSTPOOL_INDEX;
		}

		@Override
		protected int getTag() {
			return TAG_STRING;
		}

		@Override
		protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
			out.writeShort(pool.findOrAddUtf8(value));
		}


		@Override
		public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
			out.write(StringUtil.stringToLiteral(value));
		}


		@Override
		public boolean equals(Object other) {
			return this == other || other instanceof StringElementValue elementValue && this.equals(elementValue);
		}

		public boolean equals(StringElementValue other) {
			return this == other || value.equals(other.value);
		}
	}


	public static class EnumElementValue extends ElementValue implements SameDisassemblingStringifyWritable<ClassInfo> {

		private final ClassType type;
		private final String constantName;

		private EnumElementValue(ClassType type, String constantName) {
			this.type = type;
			this.constantName = constantName;
		}

		private EnumElementValue(ExtendedDataInputStream in, ConstantPool pool) {
			this.type = ClassType.fromTypeDescriptor(pool.getUtf8String(in.readUnsignedShort()));
			this.constantName = pool.getUtf8String(in.readUnsignedShort());
		}

		private EnumElementValue(AssemblingInputStream in) {
			Pair<String, String> classAndName = in.nextClassAndName();
			this.type = ClassType.fromDescriptor(classAndName.first());
			this.constantName = classAndName.second();
		}

		public ClassType getType() {
			return type;
		}

		public String getConstantName() {
			return constantName;
		}

		@Override
		protected int getDataLength() {
			return Sizes.CONSTPOOL_INDEX * 2;
		}

		@Override
		protected int getTag() {
			return TAG_ENUM;
		}

		@Override
		protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
			out .recordShort(pool.utf8IndexFor(type))
				.recordShort(pool.findOrAddUtf8(constantName));
		}


		@Override
		public void addImports(ClassInfo classinfo) {
			classinfo.addImport(type);
		}

		@Override
		public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
			out.printObject(type, classinfo).print('.').print(constantName);
		}


		@Override
		public boolean equals(Object other) {
			return this == other || other instanceof EnumElementValue elementValue && this.equals(elementValue);
		}

		public boolean equals(EnumElementValue other) {
			return this == other || this.type.equals(other.type) && this.constantName.equals(other.constantName);
		}
	}


	public static class ClassElementValue extends ElementValue implements SameDisassemblingStringifyWritable<ClassInfo> {

		private final Type type;

		private ClassElementValue(Type type) {
			this.type = type;
		}

		private ClassElementValue(ExtendedDataInputStream in, ConstantPool pool) {
			this.type = Type.parseType(pool.getUtf8String(in.readUnsignedShort()));
		}

		private ClassElementValue(AssemblingInputStream in) {
			this.type = in.nextType();
			in.requireNext(".class");
		}

		public Type getType() {
			return type;
		}

		@Override
		protected int getDataLength() {
			return Sizes.CONSTPOOL_INDEX;
		}

		@Override
		protected int getTag() {
			return TAG_CLASS;
		}

		@Override
		protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
			out.writeShort(pool.utf8IndexFor(type));
		}


		@Override
		public void addImports(ClassInfo classinfo) {
			classinfo.addImport(type);
		}

		@Override
		public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
			out.printObject(type, classinfo).print(".class");
		}


		@Override
		public boolean equals(Object other) {
			return this == other || other instanceof ClassElementValue elementValue && this.equals(elementValue);
		}

		public boolean equals(ClassElementValue other) {
			return this == other || type.equals(other.type);
		}
	}


	public static class AnnotationElementValue extends ElementValue implements SameDisassemblingStringifyWritable<ClassInfo> {

		private final Annotation annotation;

		private AnnotationElementValue(ExtendedDataInputStream in, ConstantPool pool) {
			this.annotation = Annotation.read(in, pool);
		}

		private AnnotationElementValue(Annotation annotation) {
			this.annotation = annotation;
		}

		public Annotation getAnnotation() {
			return annotation;
		}

		@Override
		protected int getDataLength() {
			return annotation.getFullLength();
		}

		@Override
		protected int getTag() {
			return TAG_ANNOTATION;
		}

		@Override
		protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
			out.write(annotation, pool);
		}


		@Override
		public void addImports(ClassInfo classinfo) {
			annotation.addImports(classinfo);
		}

		@Override
		public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
			out.printObject(annotation, classinfo);
		}


		@Override
		public boolean equals(Object other) {
			return this == other || other instanceof AnnotationElementValue elementValue && this.equals(elementValue);
		}

		public boolean equals(AnnotationElementValue other) {
			return this == other || annotation.equals(other.annotation);
		}
	}


	public static class ArrayElementValue extends ElementValue {

		private final @Immutable List<? extends ElementValue> values;

		private ArrayElementValue(ExtendedDataInputStream in, ConstantPool pool) {
			this.values = in.readImmutableList(() -> ElementValue.read(in, pool));
		}

		private ArrayElementValue(@Nullable Type elementType, AssemblingInputStream in, ConstantPool pool) {
			if (in.advanceIfHasNext('}')) {
				this.values = Collections.emptyList();

			} else {
				List<ElementValue> values = new ArrayList<>();

				Function<AssemblingInputStream, ElementValue> elementValueCreator;

				if (elementType == null) {
					elementValueCreator = inp -> parse(inp, pool);

				} else if (elementType instanceof PrimitiveType primitiveType) {
					elementValueCreator = inp -> new ConstElementValue(
							primitiveType,
							pool.get(pool.findOrAddNumber(inp.nextNumber()))
					);

				} else if (elementType.equals(ClassType.STRING)) {
					elementValueCreator = inp -> new StringElementValue(inp.nextStringLiteral());

				} else if (elementType.equals(ClassType.ENUM)) {
					elementValueCreator = EnumElementValue::new;

				} else if (elementType.equals(ClassType.CLASS)) {
					elementValueCreator = ClassElementValue::new;

				} else {
					throw new ParseException("Illegal array element type " + elementType);
				}

				do {
					values.add(elementValueCreator.apply(in));

					if (!in.advanceIfHasNext(',')) {
						in.requireNext('}');
						break;
					}

				} while (!in.advanceIfHasNext('}'));


				this.values = Collections.unmodifiableList(values);
			}
		}

		private ArrayElementValue(@Immutable List<? extends ElementValue> values) {
			this.values = values;
		}

		public @Immutable List<? extends ElementValue> getValues() {
			return values;
		}

		@Override
		protected int getDataLength() {
			return Sizes.LENGTH + values.stream().mapToInt(ElementValue::getFullLength).sum();
		}

		@Override
		protected int getTag() {
			return TAG_ARRAY;
		}

		@Override
		protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
			out.writeAll(values, pool);
		}


		@Override
		public void addImports(ClassInfo classinfo) {
			classinfo.addImportsFor(values);
		}

		@Override
		public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {

			if (values.isEmpty()) {
				out.write("{}");

			} else if (values.size() == 1) {
				out.print(values.get(0), classinfo);

			} else {
				out.print("{ ").printAll(values, classinfo, ", ").print(" }");
			}
		}

		@Override
		public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
			if (values.isEmpty()) {
				out.write("{}");

			} else {
				out.print("{ ").printAll(values, classinfo, ", ").print(" }");
			}
		}


		@Override
		public boolean equals(Object other) {
			return this == other || other instanceof ArrayElementValue elementValue && this.equals(elementValue);
		}

		public boolean equals(ArrayElementValue other) {
			return this == other || values.equals(other.values);
		}
	}


	public int getFullLength() {
		return Sizes.BYTE + getDataLength();
	}

	protected abstract int getDataLength();

	protected abstract int getTag();

	@Override
	public final void serialize(AssemblingOutputStream out, ConstantPool pool) {
		out.writeByte(getTag());
		serializeData(out, pool);
	}

	protected abstract void serializeData(AssemblingOutputStream out, ConstantPool pool);

	@Override
	public abstract boolean equals(Object other);


	protected static ElementValue read(ExtendedDataInputStream in, ConstantPool pool) {

		char tag = (char)in.readUnsignedByte();

		return switch (tag) {
			case TAG_BYTE       -> new ConstElementValue(PrimitiveType.BYTE, in, pool);
			case TAG_SHORT      -> new ConstElementValue(PrimitiveType.SHORT, in, pool);
			case TAG_CHAR       -> new ConstElementValue(PrimitiveType.CHAR, in, pool);
			case TAG_INT        -> new ConstElementValue(PrimitiveType.INT, in, pool);
			case TAG_LONG       -> new ConstElementValue(PrimitiveType.LONG, in, pool);
			case TAG_FLOAT      -> new ConstElementValue(PrimitiveType.FLOAT, in, pool);
			case TAG_DOUBLE     -> new ConstElementValue(PrimitiveType.DOUBLE, in, pool);
			case TAG_BOOLEAN    -> new ConstElementValue(PrimitiveType.BOOLEAN, in, pool);
			case TAG_STRING     -> new StringElementValue(in, pool);
			case TAG_ENUM       -> new EnumElementValue(in, pool);
			case TAG_CLASS      -> new ClassElementValue(in, pool);
			case TAG_ANNOTATION -> new AnnotationElementValue(in, pool);
			case TAG_ARRAY      -> new ArrayElementValue(in, pool);
			default -> throw new DisassemblingException("Illegal annotation element value tag: " +
					"'" + tag + "' (" + IntegerUtil.hex1WithPrefix(tag) + ")");
		};
	}

	protected static ElementValue parse(AssemblingInputStream in, ConstantPool pool) {
		if (in.advanceIfHasNext(Keywords.ENUM)) {
			return new EnumElementValue(in);
		}

		if (in.advanceIfHasNext(Keywords.TRUE)) {
			return new ConstElementValue(PrimitiveType.BOOLEAN, pool.get(pool.findOrAddInteger(1)));
		}

		if (in.advanceIfHasNext(Keywords.FALSE)) {
			return new ConstElementValue(PrimitiveType.BOOLEAN, pool.get(pool.findOrAddInteger(0)));
		}

		if (in.advanceIfHasNext('(')) {
			var type = in.nextPrimitiveType();

			if (type == PrimitiveType.VOID) {
				throw ParseException.expectedButGot("primitive type", "void");
			}

			in.requireNext(')');

			return new ConstElementValue(type, pool.get(pool.findOrAddNumber(in.nextNumber())));
		}

		if (in.advanceIfHasNext('@')) {
			return new AnnotationElementValue(new Annotation(in, pool));
		}

		if (in.advanceIfHasNext('{')) {
			return new ArrayElementValue(null, in, pool);
		}

		Type type = in.nextTypeIfExists();

		if (type != null) {
			if (in.advanceIfHasNext(".class")) {
				return new ClassElementValue(type);
			}

			if (type instanceof ArrayType arrayType && in.advanceIfHasNext('{')) {
				return new ArrayElementValue(arrayType.getElementType(), in, pool);
			}
		}

		int ch = in.tryReadCharLiteral();

		if (ch != AssemblingInputStream.EOF_CHAR) {
			return new ConstElementValue(PrimitiveType.CHAR, pool.get(pool.findOrAddInteger(ch)));
		}

		Constant constant = pool.get(in.nextLiteralConstant(pool));

		if (constant instanceof IntegerConstant integerConstant) return new ConstElementValue(PrimitiveType.INT, integerConstant);
		if (constant instanceof LongConstant longConstant)       return new ConstElementValue(PrimitiveType.LONG, longConstant);
		if (constant instanceof FloatConstant floatConstant)     return new ConstElementValue(PrimitiveType.FLOAT, floatConstant);
		if (constant instanceof DoubleConstant doubleConstant)   return new ConstElementValue(PrimitiveType.DOUBLE, doubleConstant);
		if (constant instanceof StringConstant stringConstant)   return new StringElementValue(stringConstant.getString());

		throw ParseException.expectedButGot("element value", constant.getConstantName());
	}

	protected static ElementValue fromUnknownValue(Object value) {

		if (value instanceof Constable) {

			if (value instanceof Byte num) return new ConstElementValue(num);
			if (value instanceof Short num) return new ConstElementValue(num);
			if (value instanceof Integer num) return new ConstElementValue(num);
			if (value instanceof Character chr) return new ConstElementValue(chr);
			if (value instanceof Long num) return new ConstElementValue(num);
			if (value instanceof Float num) return new ConstElementValue(num);
			if (value instanceof Double num) return new ConstElementValue(num);
			if (value instanceof Boolean bool) return new ConstElementValue(bool);

			if (value instanceof String str)
				return new StringElementValue(str);

			if (value instanceof Enum<?> en)
				return new EnumElementValue(ClassType.fromClass(en.getDeclaringClass()), en.name());

			if (value instanceof Class<?> clazz)
				return new ClassElementValue(Type.fromClass(clazz));

		} else {

			if (value instanceof java.lang.annotation.Annotation annotation)
				return new AnnotationElementValue(Annotation.fromReflectAnnotation(annotation));

			if (value.getClass().isArray()) {

				if (value instanceof Object[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> fromUnknownValue(array[i])));

				if (value instanceof byte[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> new ConstElementValue(array[i])));

				if (value instanceof short[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> new ConstElementValue(array[i])));

				if (value instanceof char[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> new ConstElementValue(array[i])));

				if (value instanceof int[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> new ConstElementValue(array[i])));

				if (value instanceof long[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> new ConstElementValue(array[i])));

				if (value instanceof float[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> new ConstElementValue(array[i])));

				if (value instanceof double[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> new ConstElementValue(array[i])));

				if (value instanceof boolean[] array)
					return new ArrayElementValue(arrayToElementValues(array.length, i -> new ConstElementValue(array[i])));
			}
		}

		throw new IllegalArgumentException("Object " + value + " is not an annotation field");
	}


	private static List<ElementValue> arrayToElementValues(int size, IntFunction<? extends ElementValue> elementSupplier) {
		List<ElementValue> list = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			list.add(elementSupplier.apply(i));
		}

		return list;
	}
}
