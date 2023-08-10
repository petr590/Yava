package x590.yava.example.decompiling.jdk;

import x590.yava.FileSource;
import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example(classes = String.class, source = FileSource.JDK)
public class StringExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.runForJdk(String.class);
	}
}
