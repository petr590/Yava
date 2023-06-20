package x590.yava.attribute.annotation;

import x590.util.annotation.Immutable;
import x590.yava.attribute.Attribute;
import x590.yava.attribute.AttributeNames;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.type.reference.ClassType;
import x590.yava.writable.StringifyWritable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AnnotationsAttribute extends Attribute implements StringifyWritable<ClassInfo> {

	private final @Immutable List<Annotation> annotations;

	public AnnotationsAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		this(name, length, Annotation.readAnnotations(in, pool));
	}

	private AnnotationsAttribute(String name, int length, @Immutable List<Annotation> annotations) {
		super(name, length);
		this.annotations = annotations;
	}


	public static AnnotationsAttribute emptyVisible() {
		return EmptyAnnotationsAttribute.VISIBLE;
	}

	public static AnnotationsAttribute emptyInvisible() {
		return EmptyAnnotationsAttribute.INVISIBLE;
	}


	public boolean isEmpty() {
		return false;
	}


	public static final class EmptyAnnotationsAttribute extends AnnotationsAttribute {

		public static final AnnotationsAttribute
				VISIBLE = new EmptyAnnotationsAttribute(AttributeNames.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS),
				INVISIBLE = new EmptyAnnotationsAttribute(AttributeNames.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS);

		private EmptyAnnotationsAttribute(String name) {
			super(name, 0, Collections.emptyList());
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public Optional<Annotation> findAnnotation(ClassType type) {
			return Optional.empty();
		}

		@Override
		public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		}
	}


	public Optional<Annotation> findAnnotation(ClassType type) {
		return annotations.stream().filter(annotation -> annotation.getType().equals(type)).findAny();
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImportsFor(annotations);
	}

	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.printEachUsingFunction(annotations, annotation -> out.printIndent().println(annotation, classinfo));
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof AnnotationsAttribute annotationsAttribute && this.equals(annotationsAttribute);
	}

	public boolean equals(AnnotationsAttribute other) {
		return this == other || annotations.equals(other.annotations);
	}
}
