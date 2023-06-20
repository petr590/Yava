package x590.yava.instruction.scope;

import x590.yava.context.DecompilationContext;
import x590.yava.context.DisassemblerContext;
import x590.yava.operation.condition.CompareBinaryOperation;
import x590.yava.operation.condition.ConditionOperation;
import x590.yava.type.primitive.PrimitiveType;

public abstract class IfICmpInstruction extends IfInstruction {

	public IfICmpInstruction(DisassemblerContext context, int offset) {
		super(context, offset);
	}

	@Override
	public ConditionOperation getCondition(DecompilationContext context) {
		return new CompareBinaryOperation(context, getCompareType(), PrimitiveType.BYTE_SHORT_INT_CHAR_BOOLEAN);
	}
}
