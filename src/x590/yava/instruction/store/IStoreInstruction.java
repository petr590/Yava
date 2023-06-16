package x590.yava.instruction.store;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.store.IStoreOperation;

public class IStoreInstruction extends StoreInstruction {
	
	public IStoreInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new IStoreOperation(context, index);
	}
}
