package x590.yava.example.scope;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
public class SynchronizedExample {

	public static void main(String... args) {
		ExampleTesting.DECOMPILING.run(SynchronizedExample.class);
	}

	public static void foo() {

		int i = 10;

		synchronized (System.out) {
			i += 20;
		}

		System.out.println(i);
	}
}
