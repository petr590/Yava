package x590.yava.operation.arraystore;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class SAStoreOperation extends ArrayStoreOperation {

	public SAStoreOperation(DecompilationContext context) {
		super(ArrayType.SHORT_ARRAY, context);
	}
}
