package x590.yava.instruction.scope;

import x590.yava.context.DecompilationContext;
import x590.yava.context.DisassemblerContext;
import x590.yava.operation.condition.CompareType.EqualsCompareType;
import x590.yava.operation.condition.CompareWithNullOperation;
import x590.yava.operation.condition.ConditionOperation;

public abstract class IfANullInstruction extends IfInstruction {

	public IfANullInstruction(DisassemblerContext context, int offset) {
		super(context, offset);
	}

	@Override
	public ConditionOperation getCondition(DecompilationContext context) {
		return new CompareWithNullOperation(context.pop(), getCompareType());
	}

	@Override
	public abstract EqualsCompareType getCompareType();
}
