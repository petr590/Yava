package x590.yava.example.debug;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class Debug4 {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(Debug4.class);
	}

	public static long test(long x) {
		return x >> 63;
	}
}
