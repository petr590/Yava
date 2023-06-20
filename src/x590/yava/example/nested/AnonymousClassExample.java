package x590.yava.example.nested;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class AnonymousClassExample {

	public String name = "Anonymous class";

	public void foo() {
	}

	public static void main(String[] args) /* throws ClassNotFoundException */ {
		ExampleTesting.DECOMPILING.run(AnonymousClassExample.class//, "-A"
//				, Class.forName(AnonymousClassExample.class.getName() + "$1")
//				, Class.forName(AnonymousClassExample.class.getName() + "$2")
		);
	}

	public Inner bar() {
		return new Inner(1, 2, 3) {

			@Override
			public String getName() {
				foo();
				return name;
			}

			@SuppressWarnings("unused")
			public String getName(int this$0) {
				foo();
				return name;
			}
		};
	}

	public static abstract class Inner {

		public Inner() {
		}

		public Inner(int arg1) {
		}

		public Inner(int... args) {
		}

		public abstract String getName();
	}
}
