package x590.yava.instruction.load;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.load.DLoadOperation;

public class DLoadInstruction extends LoadInstruction {
	
	public DLoadInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new DLoadOperation(context, index);
	}
}
