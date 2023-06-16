package x590.yava.example.other;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public enum ExampleEnum {
	A(0, "A"), B(1), C, D, E(-1, "BD");
	
	private final int value;
	private final String name;
	
	ExampleEnum() {
		this(-1);
	}
	
	ExampleEnum(int value) {
		this(value, "unknown");
	}
	
	ExampleEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ExampleEnum.class);
	}
}
