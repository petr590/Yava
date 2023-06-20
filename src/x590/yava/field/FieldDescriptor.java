package x590.yava.field;

import x590.util.annotation.Immutable;
import x590.yava.Descriptor;
import x590.yava.Importable;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.FieldrefConstant;
import x590.yava.constpool.NameAndTypeConstant;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.type.Type;
import x590.yava.type.reference.RealReferenceType;
import x590.yava.type.reference.ReferenceType;
import x590.yava.type.reference.generic.GenericDeclarationType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class FieldDescriptor extends Descriptor<FieldDescriptor> implements Importable {

	private static final Map<RealReferenceType, Map<String, Map<Type, FieldDescriptor>>> INSTANCES = new HashMap<>();


	private final Type type;

	public static FieldDescriptor from(FieldrefConstant fieldref) {
		return from(fieldref.getClassConstant().toClassType(), fieldref.getNameAndType());
	}

	public static FieldDescriptor from(RealReferenceType declaringClass, NameAndTypeConstant nameAndType) {
		return of(declaringClass, nameAndType.getNameConstant().getString(), nameAndType.getDescriptor().getString());
	}

	public static FieldDescriptor from(RealReferenceType declaringClass, ExtendedDataInputStream in, ConstantPool pool) {
		return of(declaringClass, pool.getUtf8String(in.readUnsignedShort()), pool.getUtf8String(in.readUnsignedShort()));
	}

	public static FieldDescriptor from(RealReferenceType declaringClass, AssemblingInputStream in) {
		return of(in.nextType(), declaringClass, in.nextName());
	}

	public static FieldDescriptor of(RealReferenceType declaringClass, String name, String descriptor) {
		return of(Type.parseType(descriptor), declaringClass, name);
	}

	public static FieldDescriptor of(Type type, RealReferenceType declaringClass, String name) {
		return INSTANCES
				.computeIfAbsent(declaringClass, key -> new HashMap<>())
				.computeIfAbsent(name, key -> new HashMap<>())
				.computeIfAbsent(type, key -> new FieldDescriptor(type, declaringClass, name));
	}


	private FieldDescriptor(Type type, RealReferenceType declaringClass, String name) {
		super(declaringClass, name);
		this.type = type;
	}


	public static FieldDescriptor fromReflectField(RealReferenceType declaringClass, Field field) {
		return new FieldDescriptor(Type.fromClass(field.getType()), declaringClass, field.getName());
	}

	public static FieldDescriptor fromReflectFieldGeneric(RealReferenceType declaringClass, Field field) {
		return new FieldDescriptor(Type.fromReflectType(field.getGenericType()), declaringClass, field.getName());
	}


	public Type getType() {
		return type;
	}

	@Override
	public Type getReturnType() {
		return type;
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImport(type);
	}


	@Override
	public String toString() {
		return type.getName() + " " + this.getName();
	}


	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof FieldDescriptor other && this.equals(other);
	}

	public boolean equals(FieldDescriptor other) {
		return this == other || this.equals(other.type, other.getDeclaringClass(), other.getName());
	}

	public boolean equalsIgnoreClass(FieldDescriptor other) {
		return this == other ||
				type.equals(other.type) &&
						getName().equals(other.getName());
	}

	public boolean equals(Type type, RealReferenceType declaringClass, String name) {
		return this.type.equals(type) && getDeclaringClass().equals(declaringClass) && getName().equals(name);
	}


	public void writeType(StringifyOutputStream out, ClassInfo classinfo) {
		getType().writeLeftDefinition(out, classinfo);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		out.print(getName()).print(getType(), classinfo);
	}


	@Override
	public FieldDescriptor replaceAllTypes(@Immutable Map<GenericDeclarationType, ReferenceType> replaceTable) {
		Type type = this.type.replaceAllTypes(replaceTable);
		return type == this.type ? this : of(type, getDeclaringClass(), getName());
	}
}
