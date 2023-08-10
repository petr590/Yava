package x590.yava.attribute.annotation;

import x590.yava.Importable;
import x590.yava.attribute.Sizes;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedOutputStream;
import x590.yava.serializable.JavaSerializableWithPool;
import x590.yava.writable.SameDisassemblingStringifyWritable;

public final class Element implements
		SameDisassemblingStringifyWritable<ClassInfo>, Importable, JavaSerializableWithPool {

	private final String name;
	private final ElementValue value;

	private Element(String name, ElementValue value) {
		this.name = name;
		this.value = value;
	}

	Element(ExtendedDataInputStream in, ConstantPool pool) {
		this.name = pool.getUtf8String(in.readUnsignedShort());
		this.value = ElementValue.read(in, pool);
	}

	Element(AssemblingInputStream in, ConstantPool pool) {
		this.name = in.nextName();
		this.value = ElementValue.parse(in.requireNext('='), pool);
	}

	public static Element fromUnknownValue(String name, Object value) {
		return new Element(name, ElementValue.fromUnknownValue(value));
	}


	public String getName() {
		return name;
	}

	public ElementValue getValue() {
		return value;
	}

	public int getFullLength() {
		return Sizes.CONSTPOOL_INDEX + value.getFullLength();
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		value.addImports(classinfo);
	}

	@Override
	public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
		out.print(name).print(" = ").printObject(value, classinfo);
	}

	@Override
	public void serialize(AssemblingOutputStream out, ConstantPool pool) {
		out .recordShort(pool.findOrAddUtf8(name))
			.record(value, pool);
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof Element element && this.equals(element);
	}

	public boolean equals(Element other) {
		return this == other || name.equals(other.name) && value.equals(other.value);
	}
}
