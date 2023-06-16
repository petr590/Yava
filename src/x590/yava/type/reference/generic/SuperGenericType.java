package x590.yava.type.reference.generic;

import x590.yava.io.ExtendedStringInputStream;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.ReferenceType;
import x590.util.annotation.Nullable;

/** Дженерик, ограниченный снизу */
public final class SuperGenericType extends BoundedGenericType {
	
	public SuperGenericType(ReferenceType type) {
		super(type);
	}
	
	public SuperGenericType(ExtendedStringInputStream in) {
		super(in);
	}
	
	@Override
	public @Nullable ReferenceType getSuperType() {
		return ClassType.OBJECT;
	}
	
	@Override
	protected String encodedBound() {
		return "-";
	}
	
	@Override
	protected String bound() {
		return "super";
	}
}
