package x590.yava.instruction.dup;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.other.Dup;

public class DupX2Instruction extends AbstractDupInstruction {

	@Override
	protected void dup(DecompilationContext context) {
		Dup.dupX2(context);
	}
}
