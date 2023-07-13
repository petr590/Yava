package x590.yava.example.decompiling.invoke;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example(classes = ConcatStringsExample.class, directory = ExampleTesting.VANILLA_DIR)
public class ConcatStringsInvokedynamicExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ExampleTesting.VANILLA_DIR, ConcatStringsExample.class);
	}
}
