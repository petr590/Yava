package x590.yava.example.decompiling.scope;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.example.decompiling.inheritance.SuperclassExample;

@Example
@SuppressWarnings("unused")
public class LocalsExample extends SuperclassExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(LocalsExample.class);
	}

	public void differentVars(int x) {

		if (x == 0) {
			int y = 1;
			System.out.println(x + ", " + y);
		}

		int z = -2;
		System.out.println(x + ", 0, " + z);
	}

	public void sameVar(int x) {

		int y;

		if (x != 0) {
			y = x;
		} else {
			y = -1;
		}

		System.out.println(y);
	}
}
