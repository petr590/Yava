package x590.yava.example.array;

import static x590.yava.example.array.VarargsSuperclass.*;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class VarargsExample {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(VarargsSuperclass.class, VarargsExample.class);
	}
	
	void bar() throws NoSuchMethodException, SecurityException {
		foo();
		foo(0);
		foo(0, 0);
		foo(0, 0, 0);
		foo(0, 0, 0, 0);
		foo2("foo");
		foo2("foo", Object.class.getDeclaredMethod("equals", Object.class));
		foo2("foo", Object.class.getDeclaredMethods());
//		foo3("foo", new java.lang.reflect.Method[] {});
//		foo3("foo", new java.lang.reflect.Method[] { null });
	}
}