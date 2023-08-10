package x590.yava.example.decompiling.other;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class EmptyEnumExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(EmptyEnumExample.class);
	}

	public enum ExampleEnum1 {}

	public enum ExampleEnum2 {
		;

		int f;
	}

	public enum ExampleEnum3 {
		;

		void foo() {}
	}

	public enum ExampleEnum4 {
		;

		enum Inner {}
	}

	public enum ExampleEnum5 {
		A, B;

		int f;
	}

	public enum ExampleEnum6 {
		A, B;

		void foo() {}
	}

	public enum ExampleEnum7 {
		A, B;

		static class Inner {}
	}

	public enum ExampleEnum8 {
		A, B
	}
}
