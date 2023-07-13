package x590.yava.example.decompiling.scope;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ElseIfExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ElseIfExample.class);
	}

	public static void elseIf(int x) {
		if (x == 1) {
			System.out.println("1");
		} else if (x == 2) {
			System.out.println("2");
		} else if (x == 3) {
			System.out.println("3");
		} else if (x == 4) {
			System.out.println("4");
		} else if (x == 5) {
			System.out.println("5");
		} else if (x == 6) {
			System.out.println("6");
		} else if (x == 7) {
			System.out.println("7");
		} else {
			System.out.println("!");
		}
	}
}
