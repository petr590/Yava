package x590.yava.example.invoke;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example(classes = ConcatStringsExample.class, directory = ExampleTesting.VANILLA_DIR)
public class ConcatStringsInvokedynamicExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ExampleTesting.VANILLA_DIR, ConcatStringsExample.class);
	}
}
