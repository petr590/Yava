package x590.yava.instruction.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.returning.LReturnOperation;

public class LReturnInstruction implements Instruction {

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new LReturnOperation(context);
	}
}
