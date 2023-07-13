package x590.yava.example.decompiling.other;

import x590.yava.example.decompiling.Example;
import x590.yava.example.ExampleTesting;

@Example(args = ExampleTesting.DEFAULT_DIR + "/x590/yava/example/package-info.class")
@SuppressWarnings("unused")
public class PackageInfoExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(
				ExampleTesting.DECOMPILING.getClassPath(PackageInfoExample.class.getPackageName() + ".package-info")
		);
	}
}
