package x590.yava.operation.condition;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.cmp.CmpOperation;

public abstract class CompareOperation extends ConditionOperation {

	public static ConditionOperation valueOf(DecompilationContext context, CompareType compareType) {
		return valueOf(context.pop(), compareType);
	}

	public static ConditionOperation valueOf(Operation operand, CompareType compareType) {
		return operand instanceof CmpOperation ?
				new CompareBinaryOperation((CmpOperation) operand, compareType) :
				new CompareWithZeroOperation(operand, compareType);
	}


	private final CompareType compareType;

	public CompareOperation(CompareType compareType) {
		this.compareType = compareType;
	}

	public CompareType getCompareType() {
		return compareType;
	}

	@Override
	public int getPriority() {
		return compareType.getPriority();
	}
}
