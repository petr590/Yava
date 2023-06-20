package x590.yava.attribute;

import x590.yava.Importable;
import x590.yava.JavaSerializable;
import x590.yava.attribute.Attributes.Location;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.disassembling.DisassemblingException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;

/**
 * Представляет атрибут в class файле.
 * Атрибуты может быть в классе, поле, методе, другом атрибуте
 */
public abstract class Attribute implements JavaSerializable, Importable {

	private final String name;
	private int length;

	/**
	 * Поле {@link #length} не инициализировано и должно быть инициализировано позже
	 * с помощью вызова {@link #initLength(int)}
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

	protected void serializeHeader(ExtendedDataOutputStream out) {
//		out.writeShort(nameIndex);
		out.writeInt(length);
	}

	@Override
	public void serialize(ExtendedDataOutputStream out) {
		throw new IllegalStateException("Not released yet :(");
	}
}
