package x590.yava.example.decompiling.debug;

import x590.yava.example.ExampleTesting;

import java.lang.ref.SoftReference;

@SuppressWarnings("unused")
public class Debug3<T> {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(Debug3.class);
	}

	public SoftReference<String> test(String s) {
		String s2 = s;
		return new SoftReference<>(s2);
	}
}
