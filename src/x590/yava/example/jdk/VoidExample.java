package x590.yava.example.jdk;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.FileSource;

@Example(classes = Void.class, source = FileSource.JDK)
public class VoidExample {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.runForJdk(Void.class);
	}
}
