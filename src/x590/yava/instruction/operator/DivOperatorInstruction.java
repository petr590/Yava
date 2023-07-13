package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.DivOperatorOperation;
import x590.yava.type.Type;

public class DivOperatorInstruction extends OperatorInstruction {

	public DivOperatorInstruction(String mnemonic, Type type) {
		super(mnemonic, type);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new DivOperatorOperation(type, context);
	}
}
