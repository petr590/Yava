package x590.yava.instruction.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.returning.DReturnOperation;

public class DReturnInstruction implements Instruction {

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new DReturnOperation(context);
	}
}
