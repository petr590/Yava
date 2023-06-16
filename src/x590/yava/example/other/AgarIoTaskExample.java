package x590.yava.example.other;

import x590.yava.example.ExampleTesting;

@SuppressWarnings("unused")
public class AgarIoTaskExample {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(
				"/home/winch/eclipse-workspace/Olymp/bin/olymp/test/AgarIoTask.class"
//				AgarIoTaskExample.class
		);
	}
	
	// Fixed
	public static int test() {
		int a, b, c, d;
		return d = c = b = a = 0;
	}
	
	public static void test2() {
		for(int a = 10, b = 20; a != b; a++) {
			for(int j = 0; j < 100; j++) {}
		}
	}
}
