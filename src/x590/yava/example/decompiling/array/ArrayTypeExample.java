package x590.yava.example.decompiling.array;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ArrayTypeExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ArrayTypeExample.class);
	}

	public Class<?>[] testClone() {
		return new Class[0].clone();
	}
}
