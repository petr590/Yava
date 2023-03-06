package x590.jdecompiler.attribute;

public class SyntheticAttribute extends EmptyAttribute {
	
	private static final SyntheticAttribute INSTANCE = new SyntheticAttribute();
	
	private SyntheticAttribute() {
		super(AttributeNames.SYNTHETIC);
	}
	
	public static SyntheticAttribute get(int nameIndex, String name, int length) {
		checkLength(name, length);
		return INSTANCE;
	}
}
