package x590.yava.instruction.load;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.load.ILoadOperation;

public class ILoadInstruction extends LoadInstruction {
	
	public ILoadInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new ILoadOperation(context, index);
	}
}
