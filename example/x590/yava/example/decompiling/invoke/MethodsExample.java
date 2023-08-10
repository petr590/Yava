package x590.yava.example.decompiling.invoke;

import x590.yava.example.decompiling.Example;
import x590.yava.example.decompiling.inheritance.SuperclassExample;
import x590.yava.example.ExampleTesting;

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
