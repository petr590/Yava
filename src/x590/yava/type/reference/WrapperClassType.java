package x590.yava.type.reference;

import x590.util.annotation.Nullable;
import x590.util.function.TriFunction;
import x590.yava.type.GeneralCastingKind;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

import java.util.function.BiFunction;

public final class WrapperClassType extends ClassType {

	private final PrimitiveType primitiveType;

	WrapperClassType(String encodedName, Class<?> thisClass, PrimitiveType primitiveType) {
		super(encodedName, thisClass);
		this.primitiveType = primitiveType;
	}

	public PrimitiveType getPrimitiveType() {
		return primitiveType;
	}

	private @Nullable Type castToGeneralImpl(Type other, GeneralCastingKind kind,
											 TriFunction<Type, Type, GeneralCastingKind, Type> func,
											 BiFunction<Type, GeneralCastingKind, Type> defaultFunc) {


		if (kind == GeneralCastingKind.EQUALS_COMPARISON) {
			return this == other ? this : null;
		}

		if (this == other) {
			return kind == GeneralCastingKind.TERNARY_OPERATOR ?
					this : func.apply(primitiveType, primitiveType, kind);
		}

		if (other instanceof WrapperClassType otherWrapper) {
			return func.apply(primitiveType, otherWrapper.primitiveType, kind);
		}

		if (other instanceof PrimitiveType otherPrimitive) {
			return func.apply(primitiveType, otherPrimitive, kind);
		}

		return defaultFunc.apply(primitiveType, kind);
	}

	@Override
	public @Nullable Type castToGeneralNoexcept(Type other, GeneralCastingKind kind) {
		return castToGeneralImpl(other, kind, Type::castToGeneralNoexcept, super::castToGeneralNoexcept);
	}

	@Override
	public @Nullable Type implicitCastToGeneralNoexcept(Type other, GeneralCastingKind kind) {
		return castToGeneralImpl(other, kind, Type::implicitCastToGeneralNoexcept, super::implicitCastToGeneralNoexcept);
	}
}
