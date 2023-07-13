package x590.yava.attribute;

import x590.yava.io.AssemblingInputStream;

public final class SyntheticAttribute extends EmptyDataAttribute {

	private static final SyntheticAttribute INSTANCE = new SyntheticAttribute();

	private SyntheticAttribute() {
		super(AttributeNames.SYNTHETIC);
	}

	public static SyntheticAttribute get(String name, int length) {
		checkLength(name, length);
		return INSTANCE;
	}

	public static SyntheticAttribute get(AssemblingInputStream in) {
		in.requireNext(';');
		return INSTANCE;
	}
}
