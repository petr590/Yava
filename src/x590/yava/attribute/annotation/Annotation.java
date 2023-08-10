package x590.yava.attribute.annotation;

import x590.util.annotation.Immutable;
import x590.yava.Importable;
import x590.yava.attribute.Sizes;
import x590.yava.attribute.annotation.ElementValue.AnnotationElementValue;
import x590.yava.attribute.annotation.ElementValue.ArrayElementValue;
import x590.yava.attribute.annotation.ElementValue.ClassElementValue;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.*;
import x590.yava.serializable.JavaSerializableWithPool;
import x590.yava.type.reference.ClassType;
import x590.yava.writable.DisassemblingStringifyWritable;

import java.lang.annotation.Repeatable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Annotation implements
		DisassemblingStringifyWritable<ClassInfo>, Importable, JavaSerializableWithPool {

	private static final ClassType REPEATABLE = ClassType.fromClass(Repeatable.class);

	private final ClassType type;
	private final @Immutable List<Element> elements;

	private Annotation(ClassType type, @Immutable List<Element> elements) {
		this.type = type;
		this.elements = elements;
	}

	private Annotation(ExtendedDataInputStream in, ConstantPool pool) {
		this.type = ClassType.fromTypeDescriptor(pool.getUtf8String(in.readUnsignedShort()));
		this.elements = in.readImmutableList(() -> new Element(in, pool));
	}

	Annotation(AssemblingInputStream in, ConstantPool pool) {
		this.type = in.nextClassType();

		if (in.advanceIfHasNext('(')) {

			List<Element> elements = new ArrayList<>();

			if (!in.advanceIfHasNext(')')) {
				do {
					elements.add(new Element(in, pool));
				} while (in.advanceIfHasNext(", "));
			}

			in.requireNext(')');

			this.elements = Collections.unmodifiableList(elements);

		} else {
			this.elements = Collections.emptyList();
		}
	}

	public static Annotation fromReflectAnnotation(java.lang.annotation.Annotation reflectAnnotation) {

		return new Annotation(
				ClassType.fromClass(reflectAnnotation.annotationType()),
				elementsFromReflectAnnotation(reflectAnnotation)
		);
	}

	private static @Immutable List<Element> elementsFromReflectAnnotation(java.lang.annotation.Annotation reflectAnnotation) {

		List<Element> elements = new ArrayList<>();

		for (var method : reflectAnnotation.annotationType().getDeclaredMethods()) {

			if (method.getParameterCount() == 0 && method.trySetAccessible()) {
				try {
					Object value = method.invoke(reflectAnnotation);

					if (value != null) {
						elements.add(Element.fromUnknownValue(method.getName(), value));
					}

				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					ex.printStackTrace();
				}
			}
		}

		return Collections.unmodifiableList(elements);
	}

	public static Annotation read(ExtendedDataInputStream in, ConstantPool pool) {
		return new Annotation(in, pool);
	}

	public static @Immutable List<Annotation> readAnnotations(ExtendedDataInputStream in, ConstantPool pool) {

		return in.readArrayList((annotations, i) -> {

			var annotation = new Annotation(in, pool);
			var elements = annotation.elements;

			if (elements.size() == 1) {
				var element = elements.get(0);

				if (element.getName().equals("value") &&
					element.getValue() instanceof ArrayElementValue array) {

					List<? extends ElementValue> values = array.getValues();

					if (values.size() > 1 && values.stream().allMatch(value -> value instanceof AnnotationElementValue)) {

						@SuppressWarnings("unchecked")
						var repeatedAnnotations = (List<AnnotationElementValue>) values;
						var firstType = repeatedAnnotations.get(0).getAnnotation().getType();

						if (repeatedAnnotations.stream().skip(1).allMatch(value -> value.getAnnotation().getType().equals(firstType))) {

							var foundClassinfo = ClassInfo.findIClassInfo(repeatedAnnotations.get(0).getAnnotation().getType());

							if (foundClassinfo.isPresent()) {
								var foundRepeatableAnnotation = foundClassinfo.get().findAnnotation(REPEATABLE);

								if (foundRepeatableAnnotation.isPresent()) {
									var foundValue = foundRepeatableAnnotation.get().findValue("value");

									if (foundValue.isPresent() &&
											foundValue.get() instanceof ClassElementValue classValue &&
											classValue.getType().equals(annotation.type)) {


										annotations.addAll(repeatedAnnotations.stream()
												.map(AnnotationElementValue::getAnnotation).toList());

										return;
									}
								}
							}
						}
					}
				}
			}

			annotations.add(annotation);
		});
	}

	public static List<Annotation> parseAnnotations(AssemblingInputStream in, ConstantPool pool) {
		List<Annotation> annotations = new ArrayList<>();

		while (in.advanceIfHasNext('@')) {
			annotations.add(new Annotation(in, pool));
		}

		return annotations;
	}


	public ClassType getType() {
		return type;
	}

	public @Immutable List<Element> getElements() {
		return elements;
	}


	public Optional<ElementValue> findValue(String name) {
		return elements.stream()
				.filter(element -> element.getName().equals(name))
				.map(Element::getValue).findAny();
	}


	public int getFullLength() {
		return Sizes.CONSTPOOL_INDEX +
				Sizes.LENGTH + elements.stream().mapToInt(Element::getFullLength).sum();
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImport(type);
		classinfo.addImportsFor(elements);
	}


	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.print('@').print(type, classinfo);

		if (!elements.isEmpty()) {
			out.write('(');

			if (elements.size() == 1 && elements.get(0).getName().equals("value")) {
				out.print(elements.get(0).getValue(), classinfo);
			} else {
				out.printAll(elements, classinfo, ", ");
			}

			out.write(')');
		}
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		out.printIndent().print('@').print(type, classinfo);

		if (!elements.isEmpty()) {
			out.print('(').printAll(elements, classinfo, ", ").print(')');
		}

		out.println();
	}

	@Override
	public void serialize(AssemblingOutputStream out, ConstantPool pool) {
		out .recordShort(pool.findOrAddUtf8(type.getEncodedName()))
			.recordAll(elements, pool);
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof Annotation annotation && this.equals(annotation);
	}

	public boolean equals(Annotation other) {
		return this == other || type.equals(other.type) && elements.equals(other.elements);
	}
}
