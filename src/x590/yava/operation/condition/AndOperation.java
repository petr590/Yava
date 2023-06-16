package x590.yava.operation.condition;

import x590.yava.operation.Priority;

public final class AndOperation extends BinaryConditionOperation {
	
	AndOperation(ConditionOperation operand1, ConditionOperation operand2) {
		super(operand1, operand2);
	}
	
	@Override
	protected String getOperator() {
		return inverted ? " || " : " && ";
	}
	
	@Override
	public int getPriority() {
		return inverted ? Priority.LOGICAL_OR : Priority.LOGICAL_AND;
	}
}
