package x590.yava.attribute;

import x590.yava.io.AssemblingInputStream;

public final class DeprecatedAttribute extends EmptyDataAttribute {

	private static final DeprecatedAttribute INSTANCE = new DeprecatedAttribute();

	private DeprecatedAttribute() {
		super(AttributeNames.DEPRECATED);
	}

	public static DeprecatedAttribute get(String name, int length) {
		checkLength(name, length);
		return INSTANCE;
	}

	public static DeprecatedAttribute get(AssemblingInputStream in) {
		in.requireNext(';');
		return INSTANCE;
	}
}
