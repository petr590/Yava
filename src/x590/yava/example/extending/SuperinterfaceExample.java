package x590.yava.example.extending;

import x590.yava.example.Example;

@Example
public interface SuperinterfaceExample {

	default void foo() {
	}

	default int classMethod2() {
		return 1;
	}
}
