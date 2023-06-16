package x590.yava.example.features;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
public class OmitExample {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(OmitExample.class);
	}
	
	public boolean isEmpty() {
		return true;
	}
	
	// this должен быть опущен для всех вызовов, кроме equals
	@SuppressWarnings("unused")
	public void foo(Object other) {
		if(this.isEmpty() && !this.equals(other)) {
			Class<?> clazz = this.getClass();
		}
	}
}
