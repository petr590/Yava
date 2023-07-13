package x590.yava.example.decompiling.other;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class VariableTableExample {

	public static void main(String[] arguments) {
		ExampleTesting.DECOMPILING.run(VariableTableExample.class);
	}

	public <T> T genericExample(T value) {
		return value;
	}

	public void example1() {
		int x = 5;
		System.out.println(x);
	}
}
