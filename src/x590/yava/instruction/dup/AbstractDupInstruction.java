package x590.yava.instruction.dup;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;

public abstract class AbstractDupInstruction implements Instruction {

	@Override
	public final Operation toOperation(DecompilationContext context) {
		dup(context);
		return null;
	}

	protected abstract void dup(DecompilationContext context);
}