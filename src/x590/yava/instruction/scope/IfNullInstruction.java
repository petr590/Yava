package x590.yava.instruction.scope;

import x590.yava.context.DisassemblerContext;
import x590.yava.operation.condition.CompareType;
import x590.yava.operation.condition.CompareType.EqualsCompareType;

public class IfNullInstruction extends IfANullInstruction {
	
	public IfNullInstruction(DisassemblerContext context, int offset) {
		super(context, offset);
	}
	
	@Override
	public EqualsCompareType getCompareType() {
		return CompareType.EQUALS;
	}
}
