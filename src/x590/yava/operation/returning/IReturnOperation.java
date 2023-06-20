package x590.yava.operation.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class IReturnOperation extends ReturnOperation {

	public IReturnOperation(DecompilationContext context) {
		super(PrimitiveType.BYTE_SHORT_INT_CHAR_BOOLEAN, context);
	}

	@Override
	protected String getInstructionName() {
		return "ireturn";
	}
}
