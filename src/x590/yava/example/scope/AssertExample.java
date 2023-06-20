package x590.yava.example.scope;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class AssertExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(AssertExample.class, "--brackets-around-asserts");
	}

	// Выдаёт ошибку компиляции в некоторых компиляторах
//	public static boolean $assertionsDisabled, $assertionsDisabled_0, $assertionsDisabled_1;

	public void test(Object obj) {

//		if(!$assertionsDisabled && obj == null) {
//			throw new AssertionError();
//		}

		assert (obj != null);
		assert (obj.getClass() == Object.class) : "Not pure Object";
		assert (false) : false;
	}
}
