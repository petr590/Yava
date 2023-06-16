package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.type.Type;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.OrOperatorOperation;

public class OrOperatorInstruction extends OperatorInstruction {
	
	public OrOperatorInstruction(Type type) {
		super(type);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new OrOperatorOperation(type, context);
	}
}
