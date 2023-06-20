package x590.yava.instruction.invoke;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.invoke.InvokevirtualOperation;

public final class InvokevirtualInstruction extends InvokeInstruction {

	public InvokevirtualInstruction(int index) {
		super(index);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return InvokevirtualOperation.operationOf(context, index);
	}
}
