package x590.yava.operation.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Priority;
import x590.yava.type.Type;

public final class OrOperatorOperation extends BitwiseOperatorOperation {
	
	public OrOperatorOperation(Type type, DecompilationContext context) {
		super(type, context);
	}
	
	@Override
	public String getOperator() {
		return "|";
	}
	
	@Override
	public int getPriority() {
		return Priority.BIT_OR;
	}
}
