package x590.yava.instruction.store;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.store.DStoreOperation;

public class DStoreInstruction extends StoreInstruction {
	
	public DStoreInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new DStoreOperation(context, index);
	}
}
