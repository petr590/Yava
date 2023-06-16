package x590.yava.example.jdk;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.FileSource;

@Example(classes = Enum.class, source = FileSource.JDK)
public class EnumExample {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.runForJdk(Enum.class);
	}
}