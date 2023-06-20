package x590.yava.instruction.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.arrayload.FALoadOperation;

public class FALoadInstruction extends ArrayLoadInstruction {

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new FALoadOperation(context);
	}
}
