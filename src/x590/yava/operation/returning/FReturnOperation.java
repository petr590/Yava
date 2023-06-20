package x590.yava.operation.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class FReturnOperation extends ReturnOperation {

	public FReturnOperation(DecompilationContext context) {
		super(PrimitiveType.FLOAT, context);
	}

	@Override
	protected String getInstructionName() {
		return "freturn";
	}
}
