package x590.yava.attribute;

import x590.yava.Importable;
import x590.yava.clazz.ClassInfo;
import x590.yava.exception.WriteException;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.attribute.Attributes.Location;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.disassembling.DisassemblingException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.serializable.JavaSerializableWithPool;
import x590.yava.writable.DisassemblingWritable;

/**
 * Представляет атрибут в class файле
 */
public abstract class Attribute implements JavaSerializableWithPool, DisassemblingWritable<ClassInfo>, Importable {

	private final String name;
	private int length;

	/**
	 * Поле {@link #length} не инициализировано и должно быть инициализировано позже
	 * в дочернем конструкторе с помощью вызова {@link #initLength(int)}
	 */
	protected Attribute(String name) {
		this(name, -1);
	}

	protected Attribute(String name, int length) {
		this.name = name;
		this.length = length;
	}

	protected Attribute(String name, int length, int requiredLength) {
		this(name, length);
		checkLength(name, length, requiredLength);
	}

	protected void initLength(int length) {
		if (this.length != -1) {
			throw new IllegalStateException("length already initialized");
		}

		this.length = length;
	}


	/**
	 * @throws DisassemblingException если {@code length != requiredLength}
	 */
	protected static void checkLength(String name, int length, int requiredLength) {
		if (length != requiredLength)
			throw new DisassemblingException("Length of the \"" + name + "\" attribute must be " + requiredLength + ", got " + length);
	}


	public static Attribute read(ExtendedDataInputStream in, ConstantPool pool, Location location) {
		String name = pool.getUtf8String(in.readUnsignedShort());
		int length = in.readInt();
		int pos = in.available() - length;

		Attribute attribute = AttributeType.getAttributeType(location, name).readAttribute(name, length, in, pool, location);

		if (pos == in.available()) {
			return attribute;
		} else {
			throw new DisassemblingException("Attribute \"" + name + "\" was disassembled wrong: position difference " + (pos - in.available()));
		}
	}

	public static Attribute parse(String name, AssemblingInputStream in, ConstantPool pool, Location location) {
		return AttributeType.getAttributeType(location, name).parseAttribute(name, in, pool, location);
	}

	public String getName() {
		return name;
	}

	public int getLength() {
		return length;
	}

	public int getFullLength() {
		return Short.BYTES + Integer.BYTES + length;
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		out.printIndent().printsp(name).println('{').increaseIndent();
		writeDisassembledContent(out, classinfo);
		out.reduceIndent().printIndent().println('}');
	}

	protected void writeDisassembledContent(DisassemblingOutputStream out, ClassInfo classinfo) {
		throw new RuntimeException("Attribute \"" + name + "\" yet not finished");
	}

	@Override
	public final void serialize(AssemblingOutputStream out, ConstantPool pool) {
		out .recordShort(pool.findOrAddUtf8(name))
			.writeInt(length);

		int expectedSize = out.size() + length;

		serializeData(out, pool);

		int diff = out.size() - expectedSize;

		if (diff != 0) {
			throw new WriteException("Attribute \"" + name + "\" has pos difference: "
					+ "actual size is " + (diff > 0 ? "greater" : "less") + " than expected by " + Math.abs(diff));
		}
	}

	protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
		throw new UnsupportedOperationException("Serializing for \"" + name + "\" attribute is not released yet :(");
	}
}
