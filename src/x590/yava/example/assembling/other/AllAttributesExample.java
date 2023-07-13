package x590.yava.example.assembling.other;

import x590.yava.example.decompiling.Example;

import java.io.Serializable;

@Example
public abstract class AllAttributesExample implements Comparable<AllAttributesExample>, Serializable {

	// Varargs testing
	public static void main(String... args) throws ClassCastException {}

	public Object foo(int x) {
		return switch (x) { // tableswitch
			case 1 -> new Object();
			case 2 -> "";
			case 3 -> 0;
			case 4 -> 0L;
			case 5 -> 0F;
			case 6 -> 0D;
			default -> null;
		};
	}

	public Object bar(int x) {
		return switch (x) { // lookupswitch
			case 100 -> new Object();
			case 200 -> "";
			case 300 -> 0;
			case 400 -> 0L;
			case 500 -> 0F;
			case 600 -> 0D;
			default -> null;
		};
	}

	public <T> T getObject() {
		return null;
	}

	// bridge
	@Override
	public int compareTo(AllAttributesExample other) {
		return 0;
	}

	@Deprecated
	public void deprecatedMethod() {}
}
