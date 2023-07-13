package x590.yava.example.decompiling.features;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.example.decompiling.extending.SuperinterfaceExample;
import x590.yava.example.decompiling.inheritance.SuperclassExample;

@Example
public class SuperclassOmittingExample extends SuperclassExample implements SuperinterfaceExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(SuperclassOmittingExample.class);
	}

	@Override
	public void foo() {
		super.classMethod1(1);
		SuperinterfaceExample.super.foo();
	}

	@Override
	public int classMethod2() {
		super.classMethod2();
		return SuperinterfaceExample.super.classMethod2();
	}
}
