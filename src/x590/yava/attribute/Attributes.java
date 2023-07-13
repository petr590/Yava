package x590.yava.attribute;

import x590.util.Logger;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.Importable;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.decompilation.AttributeNotFoundException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.serializable.JavaSerializableWithPool;
import x590.yava.writable.DisassemblingWritable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Представляет набор атрибутов. Хранит только уникальные атрибуты
 */
@Immutable
public final class Attributes implements JavaSerializableWithPool, DisassemblingWritable<ClassInfo>, Importable {

	public enum Location {
		CLASS, FIELD, METHOD, CODE_ATTRIBUTE, OTHER
	}


	private static final Attributes EMPTY = new Attributes(Collections.emptyList(), Collections.emptyMap());

	private final Collection<Attribute> attributes;
	private final Map<String, Attribute> attributeByName;

	private Attributes(Collection<Attribute> attributes, Map<String, Attribute> attributeByName) {
		this.attributes = attributes;
		this.attributeByName = attributeByName;
	}

	private static Attributes of(Collection<Attribute> attributes, Map<String, Attribute> attributeByName) {
		return attributes.isEmpty() && attributeByName.isEmpty() ? EMPTY : new Attributes(attributes, attributeByName);
	}

	/**
	 * Читает атрибуты из потока
	 */
	public static Attributes read(ExtendedDataInputStream in, ConstantPool pool, Location location) {
		int length = in.readUnsignedShort();
		Map<String, Attribute> attributeByName = new HashMap<>(length);

		List<Attribute> attributes = in.readArrayList(length, () -> {
			Attribute attribute = Attribute.read(in, pool, location);

			if (attributeByName.put(attribute.getName(), attribute) != null) {
				Logger.warning("Found two attributes with the same name \"" + attribute.getName() + "\"");
			}

			return attribute;
		});

		return of(attributes, attributeByName);
	}

	public static Attributes parse(AssemblingInputStream in, ConstantPool pool, Location location) {
		if (in.advanceIfHasNext('{')) {

			Map<String, Attribute> attributeByName = new HashMap<>();

			while (!in.advanceIfHasNext('}')) {
				String name = in.nextName();
				attributeByName.put(name, Attribute.parse(name, in, pool, location));
			}

			return of(attributeByName.values(), attributeByName);

		} else {
			in.requireNext(';');
			return EMPTY;
		}
	}


	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		if (attributes.isEmpty()) {
			out.write(';');
		} else {
			out .println(" {").increaseIndent()
				.printAll(attributes, classinfo, '\n')
				.reduceIndent().printIndent().println('}');
		}
	}


	public static Attributes empty() {
		return EMPTY;
	}

	public boolean isEmpty() {
		return attributes.isEmpty();
	}


	public int getFullLength() {
		return Short.BYTES + getLength();
	}

	public int getLength() {
		return attributes.stream().mapToInt(Attribute::getFullLength).sum();
	}

	@Override
	public void serialize(AssemblingOutputStream out, ConstantPool pool) {
		out.recordAll(attributes, pool);
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImportsFor(attributes);
	}


	public <A extends Attribute> A get(AttributeType<A> type) {
		return getOrThrow(type, () -> new AttributeNotFoundException(type.getName()));
	}

	@SuppressWarnings("unchecked")
	public <A extends Attribute> @Nullable A getNullable(AttributeType<A> type) {
		return (A) attributeByName.get(type.getName());
	}

	@SuppressWarnings("unchecked")
	public <A extends Attribute> A getOrDefault(AttributeType<A> type, A defaultValue) {
		return (A) attributeByName.getOrDefault(type.getName(), defaultValue);
	}

	public <A extends Attribute> A getOrDefaultEmpty(EmptyableAttributeType<A> type) {
		return getOrDefault(type, type.getEmptyAttribute());
	}

	public <A extends Attribute, T extends Throwable> A getOrThrow(AttributeType<A> type, Supplier<T> exceptionSupplier) throws T {

		@SuppressWarnings("unchecked")
		A attribute = (A) attributeByName.get(type.getName());

		if (attribute != null)
			return attribute;

		throw exceptionSupplier.get();
	}

	public boolean has(AttributeType<?> type) {
		return attributeByName.containsKey(type.getName());
	}
}
