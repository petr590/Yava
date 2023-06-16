package x590.yava.instruction.dup;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.other.Dup;

public class DupInstruction extends AbstractDupInstruction {
	
	@Override
	protected void dup(DecompilationContext context) {
		Dup.dup(context);
	}
}
