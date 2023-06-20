package x590.yava.operation.arraystore;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class DAStoreOperation extends ArrayStoreOperation {

	public DAStoreOperation(DecompilationContext context) {
		super(ArrayType.DOUBLE_ARRAY, context);
	}
}
