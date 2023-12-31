package x590.yava.example.decompiling.features;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class MultiDeclarationExample {

	// Must be inlined
	public static int x1, y1, z1;

	// Must not be inlined
	public static int x2 = 0, y2 = 0, z2 = 0;

	// Must be inlined with --c-style-array
	public static int[] x3;
	public static int y3;
	public static int[][] z3;

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(MultiDeclarationExample.class, "--c-style-array");
	}
}
