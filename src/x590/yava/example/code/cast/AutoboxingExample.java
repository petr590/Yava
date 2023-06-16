package x590.yava.example.code.cast;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
public class AutoboxingExample {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(AutoboxingExample.class, "--no-trailing-zero");
	}
	
	@SuppressWarnings("unused")
	public static final void foo() {
		Byte b = 0;
		Short s = 0;
		Character c = 0;
		Integer i = 0;
		Long l = 0l;
		Float f = 0f;
		Double d = 0d;
		Boolean z = false;
	}
}
