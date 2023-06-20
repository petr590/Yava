package x590.yava.example.scope;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ConditionExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ConditionExample.class);
	}

	public static void foo(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f) {
		System.out.println("Blablabla");

		if ((a || b) && (c || d && e || f))
			System.out.println("Null");

		System.out.println("Blablabla");
	}
}
