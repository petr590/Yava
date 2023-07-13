package x590.yava.example.decompiling.constants;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.example.decompiling.annotation.IntAnnotationExample;

@Example
@IntAnnotationExample(ConstantSearchingExample.Inner.CONSTANT2)
public class ConstantSearchingExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ConstantSearchingExample.class);
	}

	public static final int CONSTANT1 = 1;

	@IntAnnotationExample(CONSTANT1)
	public static void foo() {}

	@IntAnnotationExample(Inner.CONSTANT2)
	public static class Inner {
		@IntAnnotationExample(CONSTANT1)
		public static final int CONSTANT2 = 2;
	}
}
