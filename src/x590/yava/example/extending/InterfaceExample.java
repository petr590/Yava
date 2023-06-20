package x590.yava.example.extending;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

import java.io.Serializable;

@Example
@SuppressWarnings("unused")
public interface InterfaceExample extends Serializable {

	int VALUE = 10;

	void interfaceMethod1();

	default void interfaceMethod2() {
		interfaceMethod3();
	}

	private void interfaceMethod3() {
	}

	static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(InterfaceExample.class, "--no-print-implicit-modifiers");
	}
}
