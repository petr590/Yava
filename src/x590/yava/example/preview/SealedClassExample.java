package x590.yava.example.preview;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example(args = {
		ExampleTesting.VANILLA_DIR + "/x590/yava/example/preview/SealedParent.class",
		ExampleTesting.VANILLA_DIR + "/x590/yava/example/preview/SealedChild1.class",
		ExampleTesting.VANILLA_DIR + "/x590/yava/example/preview/SealedChild2.class"
})
public class SealedClassExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(
				ExampleTesting.VANILLA_DIR + "/x590/yava/example/preview/SealedParent.class",
				ExampleTesting.VANILLA_DIR + "/x590/yava/example/preview/SealedChild1.class",
				ExampleTesting.VANILLA_DIR + "/x590/yava/example/preview/SealedChild2.class"
		);
	}
}
