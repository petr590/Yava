package x590.yava.example.decompiling.other;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class CharsetExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(CharsetExample.class);
	}

	public static void foo() {
		System.out.println("こんにちは");
	}
}
