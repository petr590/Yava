package x590.yava.example.invoke;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.example.inheritance.SuperclassExample;

@Example
@SuppressWarnings("unused")
public class MethodsExample extends SuperclassExample {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(Example.class);
	}
	
	{
		classMethod1(0);
		classMethod2();
	}
	
	static {
		gg();
	}
	
	@Override
	public void classMethod1(int x) {
		super.classMethod1(x);
	}
	
	@Override
	public int classMethod2() {
		return super.classMethod2();
	}
	
	public static void gg() {}
}
