package x590.yava.example.decompiling.generic;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Example
@SuppressWarnings("unused")
public abstract class GenericExample<T> extends SuperGenericExample<T>
		implements Serializable, List<SuperGenericExample<? extends T>> {

	@Serial
	private static final long serialVersionUID = 1L;

	public static <S extends CharSequence> void main(String[] args) {
		ExampleTesting.DECOMPILING.run(GenericExample.class);
	}

	public List<?> field;

	@SafeVarargs
	public static <U extends Object & CharSequence & Serializable> U foo(List<? extends U> eu, List<? super U> su, U... u) {
		return eu.get(0);
	}


	public static abstract class StringGenericExample extends GenericExample<String> {
		@Serial
		private static final long serialVersionUID = 1L;
	}
}
