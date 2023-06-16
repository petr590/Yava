package x590.yava.example.generic;

import x590.yava.example.Example;
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
