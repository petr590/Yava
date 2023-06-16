package x590.yava.operation.store;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class DStoreOperation extends StoreOperation {
	
	public DStoreOperation(DecompilationContext context, int index) {
		super(PrimitiveType.DOUBLE, context, index);
	}
}
