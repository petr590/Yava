package x590.yava.instruction.store;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.store.LStoreOperation;

public class LStoreInstruction extends StoreInstruction {
	
	public LStoreInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new LStoreOperation(context, index);
	}
}
