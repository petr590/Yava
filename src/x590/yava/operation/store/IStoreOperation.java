package x590.yava.operation.store;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class IStoreOperation extends StoreOperation {
	
	public IStoreOperation(DecompilationContext context, int index) {
		super(PrimitiveType.BYTE_SHORT_INT_CHAR_BOOLEAN, context, index);
	}
}
