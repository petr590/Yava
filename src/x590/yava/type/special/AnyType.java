package x590.yava.type.special;

import x590.yava.clazz.ClassInfo;
import x590.yava.io.ExtendedOutputStream;
import x590.yava.type.BasicType;
import x590.yava.type.CastingKind;
import x590.yava.type.Type;
import x590.yava.type.TypeSize;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;

public final class AnyType extends Type {

	public static final AnyType INSTANCE = new AnyType();

	private AnyType() {
	}


	@Override
	public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
		out.printObject(ClassType.OBJECT, classinfo);
	}

	@Override
	public String toString() {
		return "AnyType";
	}

	@Override
	public String getEncodedName() {
		return "AnyType";
	}

	@Override
	public String getName() {
		return "java.lang.Object";
	}

	@Override
	public String getNameForVariable() {
		return "o";
	}

	@Override
	public TypeSize getSize() {
		return TypeSize.WORD;
	}


	@Override
	public boolean isDefinitelySubtypeOf(Type other) {
		return this == other;
	}


	@Override
	protected boolean canCastToNarrowestImpl(Type other) {
		return true;
	}

	@Override
	protected boolean canReversedCastToNarrowestImpl(Type other) {
		return true;
	}


	@Override
	protected Type castImpl(Type other, CastingKind kind) {
		return kind.isWidest() && other instanceof PrimitiveType primitiveType ?
				primitiveType.toUncertainIntegralType() :
				other;
	}

	@Override
	protected Type reversedCastImpl(Type other, CastingKind kind) {
		return castImpl(other, kind);
	}

	@Override
	public BasicType reduced() {
		return ClassType.OBJECT;
	}
}
