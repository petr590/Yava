package x590.yava.example.decompiling.jdk;

import x590.yava.FileSource;
import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example(classes = Boolean.class, source = FileSource.JDK)
public class BooleanExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.runForJdk(Boolean.class);
	}
}
