package x590.yava.example.assembling.other;

import x590.yava.example.ExampleTesting;

public class EmptyClassJasmExample {

	public static void main(String[] args) {
//		ExampleTesting.ASSEMBLING.run(EmptyClass.class);
//		ExampleTesting.DECOMPILING.run(EmptyClass.class);

		ExampleTesting.ASSEMBLING.run(ExampleTesting.ASSEMBLING.getClassPath("x590.yava.example.assembling.other.EmptyClass"));
		ExampleTesting.DECOMPILING.run(ExampleTesting.DECOMPILING.getClassPath("x590.yava.example.assembling.other.EmptyClass"));
	}
}
