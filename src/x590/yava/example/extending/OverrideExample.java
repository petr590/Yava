package x590.yava.example.extending;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.example.inheritance.SuperclassExample;
import x590.yava.example.inheritance.SuperclassOfSuperclassExample;

import java.io.Serial;

@Example
@SuppressWarnings("unused")
public abstract class OverrideExample extends SuperclassExample implements InterfaceExample {

	@Serial
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(new Class[]{SuperclassOfSuperclassExample.class, OverrideExample.class}, "-x=never");
	}

	public OverrideExample(int x) {
	}

	static {
		System.out.println();
	}

	@Override
	public void interfaceMethod1() {
	}

	@Override
	public abstract void classMethod1(int x);

	@Override
	public abstract int classMethod2();

	@Override
	public String toString() {
		return "OverrideExample";
	}

	@Override
	public int hashCode() {
		return 0x10;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof OverrideExample;
	}
}
