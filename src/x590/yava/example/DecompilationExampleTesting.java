package x590.yava.example;

import x590.util.Logger;
import x590.yava.FileSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DecompilationExampleTesting extends ExampleTesting {

	public static DecompilationExampleTesting INSTANCE = new DecompilationExampleTesting();

	private DecompilationExampleTesting() {
		super(".class");
	}


	public void runForExampleClass(Class<?> clazz) {
		Example exampleAnnotation = getExampleAnnotation(clazz);
		run(exampleAnnotation.directory(), exampleAnnotation.classes(), exampleAnnotation.args());
	}

	private static Example getExampleAnnotation(Class<?> clazz) {
		Example exampleAnnotation = clazz.getDeclaredAnnotation(Example.class);

		if (exampleAnnotation == null) {
			throw new IllegalArgumentException("Class " + clazz.getCanonicalName() + " is not annotated with @Example");
		}

		return exampleAnnotation;
	}


	public void runForExampleClasses(Class<?>... classes) {

		List<String> args = new ArrayList<>(classes.length);

		Logger.debug("classes: (" + classes.length + ") " + Arrays.toString(classes));

		int skipped = 0;

		for (Class<?> clazz : classes) {
			Example exampleAnnotation = clazz.getDeclaredAnnotation(Example.class);

			if (exampleAnnotation != null) {

				if (exampleAnnotation.source() == FileSource.JDK) {
					skipped += 1;
					Logger.debug("Class " + clazz.getName() + " provides JDK example");
					continue;
//					args.add("-jdk");
				}

				String dir = exampleAnnotation.directory();

				Class<?>[] classesToDecompile = exampleAnnotation.classes();


				if (classesToDecompile.length == 0) {
					args.add(getClassPath(dir, clazz));

				} else {
					for (Class<?> decompilingClass : classesToDecompile) {
						args.add(getClassPath(dir, decompilingClass));
					}
				}

				args.addAll(Arrays.asList(exampleAnnotation.args()));

			} else {
				skipped++;
				Logger.debug("Class " + clazz.getName() + " has no @Example annotation");
			}
		}

		Logger.debug(skipped + " classes skipped");
		Logger.debug("args: (" + args.size() + ") " + args);

		run(args.toArray(String[]::new));
	}


	public void runForJdk(Class<?> clazz) {
		runForJdk(new Class[]{clazz}, EMPTY_ARGS);
	}

	public void runForJdk(Class<?> clazz, String... otherArgs) {
		runForJdk(new Class[]{clazz}, otherArgs);
	}

	public void runForJdk(Class<?>... classes) {
		runForJdk(classes, EMPTY_ARGS);
	}

	public void runForJdk(Class<?>[] classes, String... otherArgs) {

		run(
				Stream.concat(
						Stream.of("-jdk"),
						Stream.concat(
								Arrays.stream(classes).map(Class::getName),
								Arrays.stream(otherArgs)
						)
				).toArray(String[]::new)
		);
	}
}
