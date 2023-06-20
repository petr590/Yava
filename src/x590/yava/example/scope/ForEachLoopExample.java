package x590.yava.example.scope;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class ForEachLoopExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ForEachLoopExample.class);
	}

	public void foo(Class<?>[] classes) {

		for (Class<?> clazz : classes) {
			System.out.println(clazz);
		}
	}

	public void bar(Iterable<Class<?>> classes) {

		for (Class<?> clazz : classes) {
			System.out.println(clazz);
		}
	}
}
