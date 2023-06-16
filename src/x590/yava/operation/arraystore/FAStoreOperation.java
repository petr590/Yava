package x590.yava.operation.arraystore;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class FAStoreOperation extends ArrayStoreOperation {
	
	public FAStoreOperation(DecompilationContext context) {
		super(ArrayType.FLOAT_ARRAY, context);
	}
}
