package x590.yava.example.extending;

import java.io.Serializable;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public interface InterfaceExample extends Serializable {
	
	int VALUE = 10;
	
	void interfaceMethod1();
	
	default void interfaceMethod2() {
		interfaceMethod3();
	}
	
	private void interfaceMethod3() {}
	
	static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(InterfaceExample.class, "--no-print-implicit-modifiers");
	}
}
