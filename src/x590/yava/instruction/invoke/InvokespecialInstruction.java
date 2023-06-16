package x590.yava.instruction.invoke;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.invoke.InvokespecialOperation;

public final class InvokespecialInstruction extends InvokeInstruction {
	
	public InvokespecialInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new InvokespecialOperation(context, index);
	}
}
