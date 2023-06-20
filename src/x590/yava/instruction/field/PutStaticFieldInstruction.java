package x590.yava.instruction.field;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.operation.field.PutStaticFieldOperation;

public class PutStaticFieldInstruction extends InstructionWithIndex {

	public PutStaticFieldInstruction(int index) {
		super(index);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new PutStaticFieldOperation(context, index);
	}
}
