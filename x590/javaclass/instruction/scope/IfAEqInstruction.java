package x590.javaclass.instruction.scope;

import x590.javaclass.context.DisassemblerContext;
import x590.javaclass.operation.compare.CompareType;

public class IfAEqInstruction extends IfACmpInstruction {
	
	public IfAEqInstruction(DisassemblerContext context, int offset) {
		super(context, offset);
	}
	
	@Override
	public CompareType getCompareType() {
		return CompareType.EQUALS;
	}
}