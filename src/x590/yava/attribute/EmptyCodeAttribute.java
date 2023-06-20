package x590.yava.attribute;

import x590.yava.context.DisassemblerContext;
import x590.yava.io.ExtendedDataOutputStream;

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
	public void serialize(ExtendedDataOutputStream out) {
	}
}
