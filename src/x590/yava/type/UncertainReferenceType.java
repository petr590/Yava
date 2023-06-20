package x590.yava.type;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.exception.decompilation.IllegalTypeException;
import x590.yava.io.ExtendedOutputStream;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ArrayType;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.IArrayType;
import x590.yava.type.reference.ReferenceType;

import java.util.function.Function;

/**
 * Когда ссылочный тип неизвестен точно
 */
public sealed class UncertainReferenceType extends Type {

	/**
	 * Наиболее широкий тип
	 */
	private final ReferenceType widestType;

	/**
	 * Наиболее узкий тип. Если он равен {@code null}, то {@code this}
	 * обозначает тип {@link #widestType} и любой его подтип
	 */
	private final @Nullable ReferenceType narrowestType;

	private final String encodedName;

	private UncertainReferenceType(ReferenceType widestType, @Nullable ReferenceType narrowestType) {
		this.widestType = widestType;
		this.narrowestType = narrowestType;
		this.encodedName = "UncertainClassType:" + widestType.getEncodedName() +
				(narrowestType == null ? "" : ":" + narrowestType.getEncodedName());

		check(widestType);
		check(narrowestType);
	}

	private UncertainReferenceType(ReferenceType widestType) {
		this(widestType, null);
	}

	private static UncertainReferenceType newUncertainReferenceType(ReferenceType widestType, @Nullable ReferenceType narrowestType) {
		if (widestType.isIArrayType()) {
			if (narrowestType == null)
				return new UncertainReferenceArrayType((ReferenceType & IArrayType) widestType);

			if (narrowestType.isIArrayType())
				return new UncertainReferenceArrayType((ReferenceType & IArrayType) widestType, (ReferenceType & IArrayType) narrowestType);
		}

		return new UncertainReferenceType(widestType, narrowestType);
	}

	private static UncertainReferenceType newUncertainReferenceType(ReferenceType widestType) {
		return widestType.isIArrayType() ?
				new UncertainReferenceArrayType((ReferenceType & IArrayType) widestType) :
				new UncertainReferenceType(widestType);
	}


	private void check(@Nullable ReferenceType type) {
		if (type instanceof ArrayType arrayType &&
				arrayType.getMemberType() instanceof UncertainReferenceType) {

			throw new IllegalTypeException(this.toString());
		}
	}


	public static Type getInstance(ReferenceType widestType) {
		return newUncertainReferenceType(widestType);
	}

	public static @Nullable Type getInstanceNoexcept(ReferenceType widestType, @Nullable ReferenceType narrowestType) {
		if (narrowestType != null) {
			if (widestType.equalsIgnoreSignature(narrowestType))
				return narrowestType;

			if (widestType.isDefinitelySubclassOf(narrowestType) && !narrowestType.isDefinitelySubclassOf(widestType))
				return null;
		}

		return newUncertainReferenceType(widestType, narrowestType);
	}

	public static Type getInstance(ReferenceType widestType, @Nullable ReferenceType narrowestType) {
		Type type = getInstanceNoexcept(widestType, narrowestType);

		if (type != null)
			return type;

		throw new IllegalTypeException(widestType + ", " + narrowestType);
	}


	public @Nullable ReferenceType getNarrowestType() {
		return narrowestType;
	}

	public ReferenceType getWidestType() {
		return widestType;
	}


	private ReferenceType getNonNullType() {
		return narrowestType == null ? widestType : narrowestType;
	}

	@Override
	public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
		out.printObject(getNonNullType(), classinfo);
	}

	@Override
	public String toString() {
		return narrowestType == null ?
				"(" + widestType + ")" :
				"(" + widestType + " - " + narrowestType + ")";
	}

	@Override
	public String getEncodedName() {
		return encodedName;
	}

	@Override
	public String getName() {
		return getNonNullType().getName();
	}

	@Override
	public String getNameForVariable() {
		return getNonNullType().getNameForVariable();
	}


	@Override
	public boolean isAnyReferenceType() {
		return true;
	}


	@Override
	public TypeSize getSize() {
		return TypeSize.WORD;
	}


	private static final class UncertainReferenceArrayType extends UncertainReferenceType implements IArrayType {

		private final int nestingLevel;

		private <T extends ReferenceType & IArrayType> UncertainReferenceArrayType(T widestType, T narrowestType) {
			super(widestType, narrowestType);
			this.nestingLevel = Math.min(widestType.getNestingLevel(), narrowestType.getNestingLevel());
		}

		private <T extends ReferenceType & IArrayType> UncertainReferenceArrayType(T widestType) {
			super(widestType);
			this.nestingLevel = widestType.getNestingLevel();
		}

		@Override
		public int getNestingLevel() {
			return nestingLevel;
		}

		@Override
		public Type getMemberType() {
			return getComponentType(arrayType -> arrayType.getNestedElementType(nestingLevel));
		}

		@Override
		public Type getElementType() {
			return getComponentType(IArrayType::getElementType);
		}

		private Type getComponentType(Function<IArrayType, Type> getter) {
			if (getNarrowestType() == null) {
				if (getWidestType() instanceof IArrayType arrayWidestType) {
					return getter.apply(arrayWidestType);
				} else {
					throw new IllegalTypeException(this + " is not an array");
				}
			}

			if (getNarrowestType() instanceof IArrayType arrayNarrowestType &&
					getWidestType() instanceof IArrayType arrayWidestType) {

				try {
					return newUncertainType(
							getter.apply(arrayWidestType),
							getter.apply(arrayNarrowestType)
					);
				} catch (IllegalTypeException ex) {
					throw new IllegalTypeException("Cannot get component type for " + this, ex);
				}

			} else {
				throw new IllegalTypeException(this + " is not an array");
			}
		}


		private static Type newUncertainType(Type widestType, Type narrowestType) {
			if (widestType.equals(narrowestType)) {
				return widestType;
			}

			ReferenceType referenceWidestType = asReferenceType(widestType);
			ReferenceType referenceNarrowestType = asReferenceType(narrowestType);

			if (referenceWidestType != null && referenceNarrowestType != null) {
				return newUncertainReferenceType(referenceWidestType, referenceNarrowestType);
			}

			if (widestType instanceof PrimitiveType primitiveWidestType) {
				if (narrowestType instanceof PrimitiveType primitiveNarrowestType) {
					return UncertainIntegralType.getInstance(primitiveNarrowestType, primitiveWidestType);
				}

				if (narrowestType instanceof UncertainIntegralType uncertainNarrowestType &&
						uncertainNarrowestType.canCastToWidest(widestType)) {

					return uncertainNarrowestType;
				}

			} else if (widestType instanceof UncertainIntegralType uncertainWidestType &&
					narrowestType instanceof PrimitiveType &&
					uncertainWidestType.canCastToWidest(narrowestType)) {

				return uncertainWidestType;
			}

			throw new IllegalTypeException("Cannot make uncertain type for" + widestType + " and " + narrowestType);
		}

		private static @Nullable ReferenceType asReferenceType(Type type) {
			if (type == Types.ANY_OBJECT_TYPE) {
				return ClassType.OBJECT;
			}

			if (type instanceof ReferenceType referenceType) {
				return referenceType;
			}

			if (type instanceof UncertainReferenceType) {
				throw new IllegalTypeException("Cannot normalize UncertainReferenceType " + type);
			}

			return null;
		}
	}

	@Override
	public IArrayType arrayType(int nestingLevel) {
		return new UncertainReferenceArrayType(
				widestType.arrayType(nestingLevel),
				narrowestType == null ? null : narrowestType.arrayType(nestingLevel)
		);
	}


	@Override
	protected @Nullable Type castImpl(Type other, CastingKind kind) {
		if (this.equals(other))
			return this;

		if (other instanceof ReferenceType referenceType) {

			if (referenceType.isDefinitelySubclassOf(widestType) && (narrowestType == null || narrowestType.isDefinitelySubclassOf(referenceType))) {
				return kind.isNarrowest() ?
						getInstanceNoexcept(referenceType, narrowestType) :
						getInstanceNoexcept(widestType, referenceType);
			}

			if (kind.isNarrowest() ?
					widestType.isDefinitelySubclassOf(referenceType) :
					referenceType.isDefinitelySubclassOf(narrowestType)) {

				return this;
			}

			return null;
		}

		if (other instanceof UncertainReferenceType uncertainType) {
			if (kind.isNarrowest()) {
				ReferenceType widestType = chooseNarrowestFrom(this.widestType, uncertainType.widestType);

				if (widestType != null) {
					return getInstanceNoexcept(widestType, this.narrowestType);
				}

			} else {
				ReferenceType narrowestType = chooseWidestFrom(this.narrowestType, uncertainType.narrowestType);

				if (narrowestType != null) {
					return getInstanceNoexcept(this.widestType, narrowestType);
				}
			}
		}

		return null;
	}

	private static @Nullable ReferenceType chooseNarrowestFrom(ReferenceType type1, ReferenceType type2) {
		return type1.isDefinitelySubclassOf(type2) ? type1 :
				type2.isDefinitelySubclassOf(type1) ? type2 : null;
	}

	private static @Nullable ReferenceType chooseWidestFrom(ReferenceType type1, ReferenceType type2) {
		return type1.isDefinitelySubclassOf(type2) ? type2 :
				type2.isDefinitelySubclassOf(type1) ? type1 : null;
	}

	@Override
	protected Type reversedCastImpl(Type other, CastingKind kind) {
		if (this.equals(other))
			return this;

		if (other instanceof ReferenceType referenceType) {

			if (kind.isNarrowest() ?
					referenceType.isDefinitelySubclassOf(widestType) :
					widestType.isDefinitelySubclassOf(referenceType) || narrowestType != null && referenceType.isDefinitelySubclassOf(narrowestType)) {

				return referenceType;
			}
		}

		return null;
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		getNonNullType().addImports(classinfo);
	}

	@Override
	public BasicType reduced() {
		return getNonNullType().reduced();
	}
}
