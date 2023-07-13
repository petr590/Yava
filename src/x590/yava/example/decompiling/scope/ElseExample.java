package x590.yava.example.decompiling.scope;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ElseExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ElseExample.class);
	}

	public static void foo(int x) {

		if (x == 0)
			System.out.println("Zero");
		else
			System.out.println("Not zero");
	}
}
