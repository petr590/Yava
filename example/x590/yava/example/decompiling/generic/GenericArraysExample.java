package x590.yava.example.decompiling.generic;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class GenericArraysExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(GenericArraysExample.class);
	}

	public static Class<?>[] testWildcardArray(boolean cloneArray) {
		Class<?>[] wildcardArray = getWildcardArray();
		return cloneArray ? wildcardArray.clone() : wildcardArray;
	}

	private static native Class<?>[] getWildcardArray();
}
