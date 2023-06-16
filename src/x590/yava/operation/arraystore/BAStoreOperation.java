package x590.yava.operation.arraystore;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class BAStoreOperation extends ArrayStoreOperation {
	
	public BAStoreOperation(DecompilationContext context) {
		super(ArrayType.BYTE_OR_BOOLEAN_ARRAY, context);
	}
}
