package x590.yava.example.decompiling.array;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

import static x590.yava.example.decompiling.array.VarargsSuperclass.foo;
import static x590.yava.example.decompiling.array.VarargsSuperclass.foo2;

@Example
@SuppressWarnings("unused")
public class VarargsExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(VarargsExample.class);
	}

	void bar() throws NoSuchMethodException, SecurityException {
		foo();
		foo(0);
		foo(0, 0);
		foo(0, 0, 0);
		foo(0, 0, 0, 0);
		foo(new double[] { 0, 0, 0 });
		foo2("foo");
		foo2("foo", Object.class.getDeclaredMethod("equals", Object.class));
		foo2("foo", Object.class.getDeclaredMethods());
//		foo3("foo", new java.lang.reflect.Method[] {});
//		foo3("foo", new java.lang.reflect.Method[] { null });

		bar((short)0, (short)0);

		bar((short)0);

		baz((byte)0);

		bal((char)0);
	}

	private static void bar(short x) {}

	private static void bar(short... x) {}

	private static void baz(byte x) {}

	private static void bal(char x) {}

//	private static void bar(int x) {}
//
//	private static void bar(float x) {}
//
//	private static void bar(double x) {}

	enum Test {
		A(1);

		Test(int... args) {}
	}
}
