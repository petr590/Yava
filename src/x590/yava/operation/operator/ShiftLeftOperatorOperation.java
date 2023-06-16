package x590.yava.operation.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.type.Type;

public final class ShiftLeftOperatorOperation extends ShiftOperatorOperation {
	
	public ShiftLeftOperatorOperation(Type type, DecompilationContext context) {
		super(type, context);
	}
	
	@Override
	public String getOperator() {
		return "<<";
	}
}
