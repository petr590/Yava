package x590.yava.example.scope;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example(directory = ExampleTesting.VANILLA_DIR)
@SuppressWarnings("unused")
public class Bug1Example {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(ExampleTesting.VANILLA_DIR, Bug1Example.class);
	}
	
	public Object newReflectionData(boolean x) {
		if(!x) {
			return null;
		}
		
		while(true) {
			foo();
			
			var o = new Object();
			
			if(x) {
				return o;
			}
		}
	}
	
	private static void foo() {}
}
