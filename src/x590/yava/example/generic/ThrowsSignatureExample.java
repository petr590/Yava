package x590.yava.example.generic;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ThrowsSignatureExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ThrowsSignatureExample.class);
	}

	public static <T extends Throwable> void foo(T t) throws T {
		throw t;
	}
}