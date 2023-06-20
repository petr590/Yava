package x590.yava.example.other;

import x590.util.Logger;
import x590.yava.attribute.AttributeType;
import x590.yava.clazz.JavaClass;
import x590.yava.example.ExampleTesting;
import x590.yava.io.AssemblingInputStream;
import x590.yava.main.Config;
import x590.yava.main.Yava;
import x590.yava.main.performing.AbstractPerforming.PerformingType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class EmptyClassJasmExample {

	public static void main(String[] args) {
//		ExampleTesting.ASSEMBLING.run(EmptyClass.class);

		Yava.init(PerformingType.ASSEMBLE, Config.newDefaultConfig());

		JavaClass javaClass;

		try {
			javaClass = JavaClass.parse(
					new AssemblingInputStream(
							new FileInputStream(ExampleTesting.ASSEMBLING.getClassPath(EmptyClass.class))
					)
			);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		byte[] code = javaClass.getMethods().get(0).getAttributes().get(AttributeType.CODE).getCode();

		for (byte b : code) {
			Logger.debug(Integer.toHexString(b & 0xFF));
		}
	}
}
