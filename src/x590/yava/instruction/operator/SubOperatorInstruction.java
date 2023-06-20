package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.SubOperatorOperation;
import x590.yava.type.Type;

public class SubOperatorInstruction extends OperatorInstruction {

	public SubOperatorInstruction(Type type) {
		super(type);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new SubOperatorOperation(type, context);
	}
}
