package x590.yava.instruction.load;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.load.LLoadOperation;

public class LLoadInstruction extends LoadInstruction {

	public LLoadInstruction(int index) {
		super(index);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new LLoadOperation(context, index);
	}
}
