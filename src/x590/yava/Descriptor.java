package x590.yava;

import x590.util.annotation.Immutable;
import x590.yava.clazz.ClassInfo;
import x590.yava.type.Type;
import x590.yava.type.reference.RealReferenceType;
import x590.yava.type.reference.ReferenceType;
import x590.yava.type.reference.generic.GenericDeclarationType;
import x590.yava.writable.DisassemblingWritable;

import java.util.Map;

public abstract class Descriptor<D extends Descriptor<D>> implements DisassemblingWritable<ClassInfo> {

	private final RealReferenceType declaringClass;
	private final String name;

	public Descriptor(RealReferenceType clazz, String name) {
		this.declaringClass = clazz;
		this.name = name;
	}

	@Override
	public abstract String toString();

	public RealReferenceType getDeclaringClass() {
		return declaringClass;
	}

	public String getName() {
		return name;
	}

	public abstract Type getReturnType();

	public abstract D replaceAllTypes(@Immutable Map<GenericDeclarationType, ReferenceType> replaceTable);
}
