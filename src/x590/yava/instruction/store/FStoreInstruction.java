package x590.yava.instruction.store;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.store.FStoreOperation;

public class FStoreInstruction extends StoreInstruction {
	
	public FStoreInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new FStoreOperation(context, index);
	}
}
