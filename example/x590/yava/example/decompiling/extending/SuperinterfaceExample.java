package x590.yava.example.decompiling.extending;

import x590.yava.example.decompiling.Example;

@Example
public interface SuperinterfaceExample {

	default void foo() {}

	default int classMethod2() {
		return 1;
	}
}
