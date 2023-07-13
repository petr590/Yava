package x590.yava.attribute;

import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.DisassemblingOutputStream;

/**
 * Атрибут, который не содержит никаких данных
 */
public class EmptyDataAttribute extends Attribute {

	protected EmptyDataAttribute(String name) {
		super(name, 0);
	}

	protected static void checkLength(String name, int length) {
		checkLength(name, length, 0);
	}

	public void serializeData(AssemblingOutputStream out, ConstantPool pool) {}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		out.printIndent().print(getName()).println(';');
	}
}
