package x590.yava.example.decompiling.code;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class InstanceofExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(InstanceofExample.class);
	}

	public void foo(CharSequence seq) {
		System.out.println(seq instanceof String);
	}
}
