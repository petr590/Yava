package x590.yava.example.debug;

import x590.yava.example.ExampleTesting;

@SuppressWarnings("unused")
public class Debug2<T> {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(Debug2.class);
	}

	private T[] genericArray;

	private Object[] getObjArray() {
		return new Object[] { null };
	}

	@SuppressWarnings("unchecked")
	public void setGenericArray(Object[] array) {
		Object obj = null;

		foo(null);
		foo((T[])obj);
		foo((T[])array);
		foo(genericArray);
		this.genericArray = (T[])array;
	}

	public void foo(T[] array) {}

	@SuppressWarnings("unchecked")
	public T[] test() {
		T[] array = genericArray;

		if (array == null) {
			genericArray = array = (T[])getObjArray();
		}

		return array;
	}
}
