package x590.yava.instruction.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.operator.XorOperatorOperation;
import x590.yava.type.Type;

public class XorOperatorInstruction extends OperatorInstruction {

	public XorOperatorInstruction(String mnemonic, Type type) {
		super(mnemonic, type);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new XorOperatorOperation(type, context);
	}
}
