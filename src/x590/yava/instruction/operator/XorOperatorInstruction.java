package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.type.Type;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.XorOperatorOperation;

public class XorOperatorInstruction extends OperatorInstruction {
	
	public XorOperatorInstruction(Type type) {
		super(type);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new XorOperatorOperation(type, context);
	}
}
