package x590.yava.attribute.annotation;

import x590.util.annotation.Immutable;
import x590.yava.Importable;
import x590.yava.attribute.Attribute;
import x590.yava.attribute.AttributeNames;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.writable.StringifyWritable;

import java.util.Collections;
import java.util.List;

public class ParameterAnnotationsAttribute extends Attribute {

	private final @Immutable List<ParameterAnnotations> parametersAnnotations;

	public ParameterAnnotationsAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		this(name, length, in.readImmutableList(in.readUnsignedByte(), () -> new ParameterAnnotations(in, pool)));
	}

	private ParameterAnnotationsAttribute(String name, int length, @Immutable List<ParameterAnnotations> parametersAnnotations) {
		super(name, length);
		this.parametersAnnotations = parametersAnnotations;
	}


	public static ParameterAnnotationsAttribute emptyVisible() {
		return EmptyParameterAnnotationsAttribute.VISIBLE;
	}

	public static ParameterAnnotationsAttribute emptyInvisible() {
		return EmptyParameterAnnotationsAttribute.INVISIBLE;
	}


	public boolean isEmpty() {
		return false;
	}


	public static final class EmptyParameterAnnotationsAttribute extends ParameterAnnotationsAttribute {

		public static final ParameterAnnotationsAttribute
				VISIBLE = new EmptyParameterAnnotationsAttribute(AttributeNames.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS),
				INVISIBLE = new EmptyParameterAnnotationsAttribute(AttributeNames.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS);

		private EmptyParameterAnnotationsAttribute(String name) {
			super(name, 0, Collections.emptyList());
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public void write(StringifyOutputStream out, ClassInfo classinfo, int index) {}
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImportsFor(parametersAnnotations);
	}

	public void write(StringifyOutputStream out, ClassInfo classinfo, int slot) {
		if (slot < parametersAnnotations.size() && parametersAnnotations.get(slot) != null)
			parametersAnnotations.get(slot).writeTo(out, classinfo);
	}


	public static class ParameterAnnotations implements StringifyWritable<ClassInfo>, Importable {

		private final @Immutable List<Annotation> annotations;

		public ParameterAnnotations(ExtendedDataInputStream in, ConstantPool pool) {
			this.annotations = Annotation.readAnnotations(in, pool);
		}

		@Override
		public void addImports(ClassInfo classinfo) {
			classinfo.addImportsFor(annotations);
		}

		@Override
		public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
			out.printEachUsingFunction(annotations, annotation -> out.printsp(annotation, classinfo));
		}
	}
}
