package x590.yava.instruction.load;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.load.ALoadOperation;

public class ALoadInstruction extends LoadInstruction {
	
	public ALoadInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new ALoadOperation(context, index);
	}
}
