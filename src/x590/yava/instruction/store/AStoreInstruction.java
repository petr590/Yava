package x590.yava.instruction.store;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.store.AStoreOperation;

public class AStoreInstruction extends StoreInstruction {
	
	public AStoreInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new AStoreOperation(context, index);
	}
}
