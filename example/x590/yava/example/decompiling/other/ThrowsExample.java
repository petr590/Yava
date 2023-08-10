package x590.yava.example.decompiling.other;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ThrowsExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ThrowsExample.class);
	}

	public static void foo() throws Exception {}
}