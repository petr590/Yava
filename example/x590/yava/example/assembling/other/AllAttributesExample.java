package x590.yava.example.assembling.other;

import x590.yava.example.decompiling.Example;
import x590.yava.example.decompiling.annotation.MultiAnnotationExample;

import java.io.Serializable;

@Example
public abstract class AllAttributesExample implements Comparable<AllAttributesExample>, Serializable {

	// Varargs testing
	public static void main(String... args) throws ClassCastException /* Exceptions attribute */ {}

	public Object foo(int x) {
		return switch (x) { // tableswitch
			case 1 -> new Object();
			case 2 -> "";
			case 3 -> 0;
			case 4 -> 0L;
			case 5 -> 0F;
			case 6 -> 0D;
			case 7 -> true;
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
			case 700 -> true;
			default -> null;
		};
	}

	// Signature attribute
	public <T> T getObject() {
		return null;
	}

	// bridge and synthetic
	@Override
	public int compareTo(AllAttributesExample other) {
		return 0;
	}

	// Deprecated attribute
	// RuntimeVisibleAnnotations attribute
	@Deprecated(since = "1.2.3", forRemoval = true)
	@MultiAnnotationExample(classValue = int.class)
	public void deprecatedMethod() {}
}
