package x590.yava.attribute.code;

import x590.yava.attribute.AttributeNames;
import x590.yava.attribute.Attributes;
import x590.yava.constpool.ConstantPool;
import x590.yava.context.DisassemblerContext;
import x590.yava.io.AssemblingOutputStream;

public final class EmptyCodeAttribute extends CodeAttribute {

	public static final EmptyCodeAttribute INSTANCE = new EmptyCodeAttribute();

	private EmptyCodeAttribute() {
		super(AttributeNames.CODE, 0, 0, 0, DisassemblerContext.EMPTY_DATA, ExceptionTable.empty(), Attributes.empty());
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public void serializeData(AssemblingOutputStream out, ConstantPool pool) {}
}
