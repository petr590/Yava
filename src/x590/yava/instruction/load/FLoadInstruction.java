package x590.yava.instruction.load;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.load.FLoadOperation;

public class FLoadInstruction extends LoadInstruction {
	
	public FLoadInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new FLoadOperation(context, index);
	}
}
