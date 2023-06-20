package x590.yava.instruction.scope;

import x590.yava.context.DisassemblerContext;
import x590.yava.operation.condition.CompareType;

public class IfLeInstruction extends IfInstruction {

	public IfLeInstruction(DisassemblerContext context, int offset) {
		super(context, offset);
	}

	@Override
	public CompareType getCompareType() {
		return CompareType.LESS_OR_EQUALS;
	}
}
