package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.NegOperatorOperation;
import x590.yava.type.Type;

public class NegOperatorInstruction extends OperatorInstruction {

	public NegOperatorInstruction(Type type) {
		super(type);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new NegOperatorOperation(type, context);
	}
}
