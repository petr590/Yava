package x590.yava.constpool.constvalue;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.Constant;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.Utf8Constant;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.ClassConstOperation;
import x590.yava.type.Type;
import x590.yava.type.reference.ArrayType;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.RealReferenceType;

public final class ClassConstant extends ConstValueConstant {

	private final int nameIndex;
	private Utf8Constant name;
	private @Nullable RealReferenceType referenceType;
	private @Nullable ClassType classType;
	private @Nullable ArrayType arrayType;

	public ClassConstant(ExtendedDataInputStream in) {
		this.nameIndex = in.readUnsignedShort();
	}

	public ClassConstant(int nameIndex, ConstantPool pool) {
		this.nameIndex = nameIndex;
		init(pool);
	}


	public int getNameIndex() {
		return nameIndex;
	}


	@Override
	protected void init(ConstantPool pool) {
		name = pool.get(nameIndex);
	}

	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImport(this.toReferenceType());
	}


	public Utf8Constant getNameConstant() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("ClassConstant { %s }", name);
	}

	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.print(this.toReferenceType(), classinfo).print(".class");
	}


	@Override
	public Type getType() {
		return ClassType.CLASS;
	}

	public RealReferenceType toReferenceType() {
		if (referenceType != null)
			return referenceType;

		referenceType = Type.parseRealReferenceType(name.getString());

		if (referenceType.isClassType())
			classType = (ClassType) referenceType;
		else if (referenceType.isArrayType())
			arrayType = (ArrayType) referenceType;

		return referenceType;
	}

	public ClassType toClassType() {
		if (classType != null)
			return classType;

		referenceType = classType = ClassType.fromDescriptor(name.getString());

		return classType;
	}

	public ArrayType toArrayType() {
		if (arrayType != null)
			return arrayType;

		referenceType = arrayType = ArrayType.fromDescriptor(name.getString());

		return arrayType;
	}

	@Override
	public String getConstantName() {
		return "Class";
	}

	@Override
	public Operation toOperation() {
		return new ClassConstOperation(this);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo, Type type, int flags) {
		out.print(toReferenceType(), classinfo);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out.recordByte(Constant.TAG_CLASS).writeShort(nameIndex);
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof ClassConstant constant && this.equals(constant);
	}

	public boolean equals(ClassConstant other) {
		return this == other || name.equals(other.name);
	}
}
