package x590.yava.instruction.arraystore;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.arraystore.CAStoreOperation;

public class CAStoreInstruction extends ArrayStoreInstruction {

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new CAStoreOperation(context);
	}
}
