package x590.yava.operation.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class LReturnOperation extends ReturnOperation {

	public LReturnOperation(DecompilationContext context) {
		super(PrimitiveType.LONG, context);
	}

	@Override
	protected String getInstructionName() {
		return "lreturn";
	}
}
