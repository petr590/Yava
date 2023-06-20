package x590.yava.type.primitive;

import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;

public final class ShortType extends IntegralType {

	public static final ShortType INSTANCE = new ShortType();

	private ShortType() {
		super("S", "short", "s");
	}

	@Override
	public ClassType getWrapperType() {
		return ClassType.SHORT;
	}

	@Override
	public int getCapacity() {
		return SHORT_CAPACITY;
	}

	@Override
	public Type toUncertainIntegralType() {
		return SHORT_INT;
	}
}
