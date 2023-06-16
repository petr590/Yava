package x590.yava.operation.store;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class LStoreOperation extends StoreOperation {
	
	public LStoreOperation(DecompilationContext context, int index) {
		super(PrimitiveType.LONG, context, index);
	}
}
