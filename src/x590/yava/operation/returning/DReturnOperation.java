package x590.yava.operation.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class DReturnOperation extends ReturnOperation {

	public DReturnOperation(DecompilationContext context) {
		super(PrimitiveType.DOUBLE, context);
	}

	@Override
	protected String getInstructionName() {
		return "dreturn";
	}
}
