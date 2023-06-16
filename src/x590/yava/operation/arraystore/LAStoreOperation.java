package x590.yava.operation.arraystore;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class LAStoreOperation extends ArrayStoreOperation {
	
	public LAStoreOperation(DecompilationContext context) {
		super(ArrayType.LONG_ARRAY, context);
	}
}
