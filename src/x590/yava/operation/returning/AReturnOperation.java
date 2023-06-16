package x590.yava.operation.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.type.Types;

public final class AReturnOperation extends ReturnOperation {
	
	public AReturnOperation(DecompilationContext context) {
		super(Types.ANY_OBJECT_TYPE, context);
	}
	
	@Override
	protected String getInstructionName() {
		return "areturn";
	}
}
