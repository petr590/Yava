package x590.yava.example.decompiling.other;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ModifiersExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ModifiersExample.class);
	}

	private static final synchronized native void method1();

	private static final synchronized void method2() {}
}
