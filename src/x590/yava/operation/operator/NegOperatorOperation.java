package x590.yava.operation.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Priority;
import x590.yava.type.Type;

public final class NegOperatorOperation extends UnaryOperatorOperation {

	public NegOperatorOperation(Type type, DecompilationContext context) {
		super(type, context);
	}

	@Override
	public String getOperator() {
		return "-";
	}

	@Override
	public int getPriority() {
		return Priority.UNARY_MINUS;
	}
}
