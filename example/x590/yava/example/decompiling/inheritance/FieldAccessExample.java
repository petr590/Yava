package x590.yava.example.decompiling.inheritance;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class FieldAccessExample extends SuperclassExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(FieldAccessExample.class);
	}

	public boolean getFlag() {
		return flag;
	}
}
