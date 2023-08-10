package x590.yava.example.decompiling.code;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class InitializationExample {

	public static final int CONST1, CONST2;

	static {
		if (Math.random() > 0.5)
			CONST1 = 1;
		else
			CONST1 = 0;

		CONST2 = Math.random() > 0.5 ? 1 : 0;
	}


	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(InitializationExample.class);
	}
}
