package x590.yava.example.decompiling.jdk;

import x590.yava.FileSource;
import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example(classes = Void.class, source = FileSource.JDK)
public class VoidExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.runForJdk(Void.class);
	}
}
