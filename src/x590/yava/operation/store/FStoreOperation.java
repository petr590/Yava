package x590.yava.operation.store;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class FStoreOperation extends StoreOperation {
	
	public FStoreOperation(DecompilationContext context, int index) {
		super(PrimitiveType.FLOAT, context, index);
	}
}
