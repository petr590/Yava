package x590.yava.example.annotation;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

import java.util.ArrayList;
import java.util.List;

@Example
public abstract class AnnotationsExample {

	@InvisibleAnnotationExample(@VisibleAnnotationExample(value = 2, array = @IntAnnotationExample(10)))
	@RepeatableAnnotationExampleContainer({
			@RepeatableAnnotationExample(1),
			@RepeatableAnnotationExample(2)
	})
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(AnnotationsExample.class);
	}


	@MethodAnnotationExample
	public abstract @TypeUseAnnotationExample int foo(
			@RepeatableAnnotationExampleContainer({
					@RepeatableAnnotationExample(1),
					@RepeatableAnnotationExample(2)
			})
			@ParameterAnnotationExample
			int value);

	@MethodAnnotationExample
	public static List<@TypeUseAnnotationExample String> bar(@ParameterAnnotationExample int size) {
		return new ArrayList<>(size);
	}
}
