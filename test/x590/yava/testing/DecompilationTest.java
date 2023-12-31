package x590.yava.testing;

import org.junit.Test;
import x590.util.annotation.Nullable;
import x590.yava.example.ExampleTesting;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DecompilationTest {

	@Test
	public void testAllExamples() {
		try (var out = new PrintStream("/tmp/decompilation-testing.log")) {

			System.setOut(out);
			System.setErr(out);

			ExampleTesting.DECOMPILING.runForExampleClasses(
					findAllClassesAsStreamInPackage(ExampleTesting.class.getPackageName()).toArray(Class[]::new)
			);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * @return Набор классов в пакете и всех подпакетах
	 */
	public static Set<Class<?>> findAllClassesInPackage(String packageName) {
		return findAllClassesAsStreamInPackage(packageName).collect(Collectors.toSet());
	}

	/**
	 * @return Поток классов в пакете и всех подпакетах
	 */
	public static Stream<Class<?>> findAllClassesAsStreamInPackage(String packageName) {
		return findAllClassNamesAsStreamInPackage(packageName).map(DecompilationTest::findClass);
	}


	/**
	 * @param packageName имя пакета в формате "java.lang"
	 * @return Набор полных имён классов в пакете и всех подпакетах в виде "java.lang.Object"
	 */
	public static Set<String> findAllClassNamesInPackage(String packageName) {
		return findAllClassNamesAsStreamInPackage(packageName).collect(Collectors.toSet());
	}

	/**
	 * @param packageName имя пакета в формате "java.lang"
	 * @return Поток полных имён классов в пакете и всех подпакетах в виде "java.lang.Object"
	 */
	public static Stream<String> findAllClassNamesAsStreamInPackage(String packageName) {
		return findAllClassNamesAsStreamInPackage(packageName.replace('.', '/'),
				className -> packageName + '.' + className.substring(0, className.lastIndexOf('.')),
				subPackageName -> findAllClassNamesAsStreamInPackage(packageName + '.' + subPackageName));
	}


	/**
	 * @param packageName имя пакета в формате "java.lang" или "java/lang"
	 * @return Набор полных имён классов в пакете и всех подпакетах в виде "java/lang/Object.class"
	 */
	public static Set<String> findAllClassPathsInPackage(String packageName) {
		return findAllClassPathsAsStreamInPackage(packageName).collect(Collectors.toSet());
	}

	/**
	 * @param packageName имя пакета в формате "java.lang" или "java/lang"
	 * @return Поток полных имён классов в пакете и всех подпакетах в виде "java/lang/Object.class"
	 */
	public static Stream<String> findAllClassPathsAsStreamInPackage(String packageName) {
		var packagePath = packageName.replace('.', '/');
		return findAllClassNamesAsStreamInPackage(packagePath,
				className -> packagePath + '/' + className,
				subPackageName -> findAllClassPathsAsStreamInPackage(packagePath + '/' + subPackageName));
	}


	/**
	 * @return Поток полных имён классов в пакете и всех подпакетах в виде "java.lang.Object"
	 */
	private static Stream<String> findAllClassNamesAsStreamInPackage(String packagePath,
																	 Function<String, String> converter, Function<String, Stream<String>> recursiveFinder) {

		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packagePath);

		if (stream == null)
			return Stream.empty();

		var reader = new BufferedReader(new InputStreamReader(stream));

		Map<Boolean, List<String>> partitioned = reader.lines()
				.collect(Collectors.partitioningBy(line -> line.endsWith(".class")));

		return partitioned.get(Boolean.FALSE).stream()
				.filter(packageName -> !packageName.contains("."))
				.map(recursiveFinder)
				.reduce(
						partitioned.get(Boolean.TRUE).stream().map(converter),
						Stream::concat
				);
	}

	private static @Nullable Class<?> findClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException ex) {
			System.err.println("Failed to load class \"" + name + "\"");
		}

		return null;
	}
}
