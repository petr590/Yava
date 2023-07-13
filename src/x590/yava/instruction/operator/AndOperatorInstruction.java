package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.AndOperatorOperation;
import x590.yava.type.Type;

public class AndOperatorInstruction extends OperatorInstruction {

	public AndOperatorInstruction(String mnemonic, Type type) {
		super(mnemonic, type);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new AndOperatorOperation(type, context);
	}
}
