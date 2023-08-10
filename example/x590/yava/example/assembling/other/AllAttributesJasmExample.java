package x590.yava.example.assembling.other;

import x590.yava.example.ExampleTesting;

public class AllAttributesJasmExample {

	public static void main(String[] args) {
//		ExampleTesting.ASSEMBLING.run(AllAttributesExample.class);
		ExampleTesting.ASSEMBLING.run(
				ExampleTesting.ASSEMBLING.getClassPath("x590.yava.example.assembling.other.AllAttributesExample")
		);
	}
}
