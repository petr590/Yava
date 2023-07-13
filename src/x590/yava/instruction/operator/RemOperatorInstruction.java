package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.RemOperatorOperation;
import x590.yava.type.Type;

public class RemOperatorInstruction extends OperatorInstruction {

	public RemOperatorInstruction(String mnemonic, Type type) {
		super(mnemonic, type);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new RemOperatorOperation(type, context);
	}
}
