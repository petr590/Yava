package x590.yava.example.code.cast;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class CastExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(CastExample.class);
	}

	public static void foo(Object obj) {
		String str = (String) obj;
	}
}
