package x590.yava.instruction.dup;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.other.Dup;

public class Dup2X1Instruction extends AbstractDupInstruction {
	
	@Override
	protected void dup(DecompilationContext context) {
		Dup.dup2X1(context);
	}
}
