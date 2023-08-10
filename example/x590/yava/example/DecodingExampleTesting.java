package x590.yava.example;

import java.util.Arrays;
import java.util.stream.Stream;

public class DecodingExampleTesting extends ExampleTesting {

	protected DecodingExampleTesting(String postfix) {
		super(postfix);
	}

	public void runForJdk(Class<?> clazz) {
		runForJdk(new Class[] { clazz }, EMPTY_ARGS);
	}

	public void runForJdk(Class<?> clazz, String... otherArgs) {
		runForJdk(new Class[] { clazz }, otherArgs);
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
