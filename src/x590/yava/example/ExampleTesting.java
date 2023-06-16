package x590.yava.example;

import static java.io.File.separatorChar;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import x590.yava.main.Yava;
import x590.yava.main.Main;

// Temporary
@SuppressWarnings("unused")
public abstract class ExampleTesting {

	public static final DecompilationExampleTesting DECOMPILING = DecompilationExampleTesting.INSTANCE;
	public static final ExampleTesting ASSEMBLING = AssemblongExampleTesting.INSTANCE;


	private final String postfix;

	ExampleTesting(String postfix) {
		this.postfix = postfix;
	}

	public static final String
			// IntelliJ IDEA
			DEFAULT_DIR = "out/production/yava",
			VANILLA_DIR = "out/vanilla-example/yava";

			// Eclipse IDE
//			DEFAULT_DIR = "bin",
//			VANILLA_DIR = "vbin";

	static final String[] EMPTY_ARGS = {};


	public String getClassPath(Class<?> clazz) {
		return getClassPath(DEFAULT_DIR, clazz);
	}

	public String getClassPath(String className) {
		return getClassPath(DEFAULT_DIR, className);
	}

	public String getClassPath(String dir, Class<?> clazz) {
		return getClassPath(dir, clazz.getName());
	}

	public String getClassPath(String dir, String className) {
		return dir + separatorChar + className.replace('.', separatorChar) + postfix;
	}

	private Function<Class<?>, String> classToClassPath(String dir) {
		return clazz -> getClassPath(dir, clazz);
	}


	public void run(Class<?> clazz) {
		run(DEFAULT_DIR, clazz);
	}

	public void run(Class<?>... classes) {
		run(DEFAULT_DIR, classes);
	}

	public void run(Class<?> clazz, String... otherArgs) {
		run(DEFAULT_DIR, clazz, otherArgs);
	}

	public void run(Class<?>[] classes, String... otherArgs) {
		run(DEFAULT_DIR, classes, otherArgs);
	}

	public void run(String dir, Class<?> clazz) {
		run(getClassPath(dir, clazz));
	}

	// class0 нужен для избежания неоднозначности при вызове перегруженного метода
	public void run(String dir, Class<?> class0, Class<?>... classes) {
		run(
				Stream.concat(Stream.of(class0), Arrays.stream(classes))
						.map(classToClassPath(dir)).toArray(String[]::new)
		);
	}

	public void run(String dir, Class<?> clazz, String... otherArgs) {
		run(
				Stream.concat(
						Stream.of(getClassPath(dir, clazz)),
						Arrays.stream(otherArgs)
				).toArray(String[]::new)
		);
	}

	public void run(String dir, Class<?>[] classes, String... otherArgs) {
		run(
				Stream.concat(
						Arrays.stream(classes).map(clazz -> getClassPath(dir, clazz)),
						Arrays.stream(otherArgs)
				).toArray(String[]::new)
		);
	}

	public void run(String... args) {
		run(true, args);
	}

	public void run(boolean isDebug, String... args) {

		Yava.setDebug(isDebug);

		try {
			Main.main(args);

		} catch(Throwable ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
