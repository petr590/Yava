package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.ShiftLeftOperatorOperation;
import x590.yava.type.Type;

public class ShiftLeftOperatorInstruction extends OperatorInstruction {
	
	public ShiftLeftOperatorInstruction(Type type) {
		super(type);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new ShiftLeftOperatorOperation(type, context);
	}
}