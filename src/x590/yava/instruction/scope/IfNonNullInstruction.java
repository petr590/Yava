package x590.yava.instruction.scope;

import x590.yava.context.DisassemblerContext;
import x590.yava.operation.condition.CompareType;
import x590.yava.operation.condition.CompareType.EqualsCompareType;

public class IfNonNullInstruction extends IfANullInstruction {
	
	public IfNonNullInstruction(DisassemblerContext context, int offset) {
		super(context, offset);
	}
	
	@Override
	public EqualsCompareType getCompareType() {
		return CompareType.NOT_EQUALS;
	}
}
