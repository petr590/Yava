package x590.yava.type.primitive;

import x590.yava.type.Type;
import x590.yava.type.TypeSize;

/**
 * Целочисленный знаковый тип, который занимает 4 байта в стеке
 * ({@code int}, {@code short} и {@code byte}).<br>
 * {@code boolean} и {@code char} не включены в этот список, они обрабатываются отдельно
 */
public abstract sealed class IntegralType extends PrimitiveType
		permits ByteType, ShortType, IntType {
	
	public IntegralType(String encodedName, String name, String nameForVariable) {
		super(encodedName, name, nameForVariable);
	}
	
	@Override
	public final TypeSize getSize() {
		return TypeSize.WORD;
	}
	
	/** Размер примитива в байтах */
	public abstract int getCapacity();
	
	
	@Override
	protected boolean canCastToNarrowestImpl(Type other) {
		return this == other ||
				other instanceof IntegralType integralType && integralType.getCapacity() >= this.getCapacity();
	}
	
	@Override
	protected boolean canCastToWidestImpl(Type other) {
		return this == other ||
				other instanceof IntegralType integralType && integralType.getCapacity() <= this.getCapacity() ||
				other == CHAR && this.getCapacity() > CHAR_CAPACITY;
	}
	
	@Override
	public boolean canImplicitCastToNarrowest(Type other) {
		return canCastToNarrowest(other) || other.isLongOrFloatOrDouble();
	}
}
