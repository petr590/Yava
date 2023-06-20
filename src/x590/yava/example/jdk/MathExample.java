package x590.yava.example.jdk;

import x590.yava.FileSource;
import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example(classes = Math.class, source = FileSource.JDK)
public class MathExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.runForJdk(Math.class, "--no-trailing-zero");
	}
}
