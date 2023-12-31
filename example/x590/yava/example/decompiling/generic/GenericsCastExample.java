package x590.yava.example.decompiling.generic;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Example
@SuppressWarnings({"unused", "unchecked"})
public class GenericsCastExample<U> {

	public static void main(String[] args) {
//		ExampleTesting.DECOMPILING.run(ExampleTesting.VANILLA_DIR, GenericsCastExample.class);
		ExampleTesting.DECOMPILING.run(ExampleTesting.DEFAULT_DIR, GenericsCastExample.class);
	}

	public static <T> T uncheckedCasting1(Object t) {
		return (T) t;
	}

	public static <T> T uncheckedCasting2(String t) {
		return (T) t;
	}

	public static <T extends Number> T uncheckedCasting3(String t) {
		return (T) (Object) t;
	}

	public static <T> List<T> uncheckedCasting(List<?> t) {
		return (List<T>) t;
	}

	public static <T> T[] uncheckedCastingArray1(Object[] t) {
		return (T[]) t;
	}

	public static <T extends Serializable> T uncheckedCastingArray2(Object[] t) {
		return (T) t;
	}

	public static <T extends Cloneable> T uncheckedCastingArray3(Object[] t) {
		return (T) t;
	}

	public U[] uncheckedCastingArray4() {
		U[] arr = getArray();
		return arr != null ? arr.clone() : null;
	}

	public String[] cloneArray1(String[] arr) {
		return arr.clone();
	}

	public String[] cloneArray2(String[] arr) {
		return Arrays.copyOf(arr, arr.length);
	}

	public int[] cloneArray(int[] arr) {
		return arr.clone();
	}

	private U[] getArray() {
		return (U[]) (Math.random() > 0.5 ? new Object[0] : null);
	}
}
