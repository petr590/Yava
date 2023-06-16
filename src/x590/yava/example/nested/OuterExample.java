package x590.yava.example.nested;

import x590.yava.example.Example;
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
