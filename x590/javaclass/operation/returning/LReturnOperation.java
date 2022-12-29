package x590.javaclass.operation.returning;

import x590.javaclass.context.DecompilationContext;
import x590.javaclass.type.PrimitiveType;

public class LReturnOperation extends ReturnOperation {
	
	public LReturnOperation(DecompilationContext context) {
		super(PrimitiveType.LONG, context);
	}
	
	@Override
	protected String getInstructionName() {
		return "lreturn";
	}
}