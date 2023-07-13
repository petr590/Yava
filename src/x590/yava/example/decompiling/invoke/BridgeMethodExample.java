package x590.yava.example.decompiling.invoke;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class BridgeMethodExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(BridgeMethodExample.class, "-b");
	}

	public class Child extends Parent {

		@Override
		public String getState() {
			return "";
		}
	}

	public class Parent {

		public Object getState() {
			return new Object();
		}
	}
}
