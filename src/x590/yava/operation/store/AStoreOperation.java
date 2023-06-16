package x590.yava.operation.store;

import x590.yava.context.DecompilationContext;
import x590.yava.type.Types;
import x590.yava.variable.Variable;

public final class AStoreOperation extends StoreOperation {
	
	public AStoreOperation(DecompilationContext context, int index) {
		super(Types.ANY_OBJECT_TYPE, context, index);
	}
	
	@Override
	public Variable getStoringVariable() {
		return variable;
	}
}
