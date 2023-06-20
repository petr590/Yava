package x590.yava.type.primitive;

import x590.yava.type.TypeSize;
import x590.yava.type.reference.ClassType;

public final class DoubleType extends PrimitiveType {

	public static final DoubleType INSTANCE = new DoubleType();

	private DoubleType() {
		super("D", "double", "d");
	}

	@Override
	public ClassType getWrapperType() {
		return ClassType.DOUBLE;
	}

	@Override
	public TypeSize getSize() {
		return TypeSize.LONG;
	}
}
