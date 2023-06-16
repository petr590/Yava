package x590.yava.example.jdk;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.FileSource;

@Example(classes = Class.class, source = FileSource.JDK)
public class ClassExample {
	
	public static void main(String[] args) throws ClassNotFoundException {
		
		ExampleTesting.DECOMPILING.runForJdk(Class.class
				, Class.forName("java.lang.Class$1")
				, Class.forName("java.lang.Class$2")
				, Class.forName("java.lang.Class$3")
		);
		
//		ExampleTesting.runDecompiler(
//				"/home/winch/0x590/java/jdk-8-rt/java/lang/Class.class",
//				"/home/winch/0x590/java/jdk-8-rt/java/lang/Class$1.class"
//		);
//		ExampleTesting.runDecompilerForJdk(Class.class);
	}
}
