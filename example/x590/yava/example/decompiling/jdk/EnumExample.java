package x590.yava.example.decompiling.jdk;

import x590.yava.FileSource;
import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example(classes = Enum.class, source = FileSource.JDK)
public class EnumExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.runForJdk(Enum.class);
	}
}
