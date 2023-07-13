package x590.yava.example.decompiling.code;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class StackExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(StackExample.class);
	}

	// Баг с пустым стеком, исправлено
	public void foo(int x, int y) {
		throw new IllegalArgumentException("#" + (x <= 0 ? "a" : "b"));
	}
}
