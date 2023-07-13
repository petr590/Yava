package x590.yava.example.decompiling.other;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

import java.io.Serializable;

@Example
@SuppressWarnings("unused")
public record RecordExample<T>(int x, int y, T obj) implements Serializable {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(RecordExample.class, "-A");
	}

	public RecordExample(int x, int y, T obj) {
		this.x = x;
		this.y = -1;
		this.obj = obj;
	}

	public RecordExample(T obj) {
		this(0, 0, obj);
	}

	public int x() {
		return x;
	}

	int foo() {
		return x - 1;
	}
}
