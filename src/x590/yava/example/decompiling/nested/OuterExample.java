package x590.yava.example.decompiling.nested;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class OuterExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(OuterExample.class);
	}

	static class Middle {
		static class Inner {}
	}
}
