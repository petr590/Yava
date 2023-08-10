package x590.yava.example.decompiling.nested;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

import java.util.Collections;
import java.util.Map;

@Example
@SuppressWarnings("unused")
public class NestedClassExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(
				NestedClassExample.class,
				NonstaticInner.class,
				NonstaticInnerChild.class,
				StaticInner.class,
				InnerEnum.class,
				InnerInterface.class
		);
	}

	public class NonstaticInner {
		public NonstaticInner() {
			this(Collections.emptyMap());
		}

		public NonstaticInner(Map<String, Integer> m) {}
	}

	public class NonstaticInnerChild extends NonstaticInner {
		public NonstaticInnerChild() {}

		public NonstaticInnerChild(Map<String, Integer> m) {
			super(m);
		}
	}

	public static class StaticInner {
		public StaticInner() {}

		public StaticInner(Map<String, Integer> m) {}
	}

	public enum InnerEnum {
		A, B, C
	}

	public interface InnerInterface {}
}
