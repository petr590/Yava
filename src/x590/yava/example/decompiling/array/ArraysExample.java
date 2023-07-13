package x590.yava.example.decompiling.array;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ArraysExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ArraysExample.class, "--c-style-array");
	}

	public int[] arr1    = { 0, 0, 0, 0, 0 };
	public int[][] arr2  = {{ 0, 0, 0, 0, 0 }};
	public long[] arrL = { 1, 2, 3, 4, 5 };
	public float[] arrF = { 1, 2, 3, 4, 5 };
	public double[] arrD = { 1, 2, 3, 4, 5 };
	public String[] arr3 = { "A", null, "null", null };
	public String[][] arr4 = { {"A", null, "null"}, null };


	public static int[] otherArray;
	public static int[] anotherArray;
	public static int anotherValue;

	public void foo(int g) {
		int[] x;

		if (g == 0) {
			x = new int[9];
		} else {
			x = new int[1];
		}

		System.out.println(x[0] + x.length);
	}


	// Класс с цифрой в конце названия
	private static class Class1 {}

	// Названия переменных после декомпиляции должны быть такими же
	public static void testNames() {
		int[] intArray = {};
		int[][] int2dArray = {};
		int[][][] int3dArray = {};
		String[][] string2dArray = {};
		Class1[][] class1_2dArray = {};
	}
}
