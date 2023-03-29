package x590.jdecompiler.example;

import static x590.jdecompiler.example.VarargsSuperclass.*;

@Example
public class VarargsExample {
	
	public static void main(String[] args) {
		ExampleTesting.runDecompiler(VarargsSuperclass.class, VarargsExample.class);
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
//		foo3("foo", new Class[] {});
//		foo3("foo", new Class[] { Object.class });
	}
}