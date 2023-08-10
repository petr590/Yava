package x590.yava.example.decompiling.features;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

import java.io.IOException;

@Example
@SuppressWarnings("unused")
public class NameInferenceExample {

	public static void main(String[] args) {

		try {
			Process process = Runtime.getRuntime().exec(new String[] {
					"javac-19",
					"-d", ExampleTesting.VANILLA_DIR,
					"-cp", ExampleTesting.DEFAULT_DIR,
					"src/" + NameInferenceExample.class.getName().replace('.', '/') + ".java"
			});

			process.getInputStream().transferTo(System.out);
			process.getErrorStream().transferTo(System.err);

			int exitStatus = process.waitFor();

			if(exitStatus != 0) {
				throw new RuntimeException("Exit status = " + exitStatus);
			}

		} catch (IOException | InterruptedException ex) {
			throw new RuntimeException(ex);
		}

		ExampleTesting.DECOMPILING.run(ExampleTesting.VANILLA_DIR, NameInferenceExample.class);
	}

	/** Названия переменных в этом методе должны соответствовать названиям после декомпиляции */
	public void test(String str) {
		int length = str.length();
	}
}
