package x590.yava.operation.arraystore;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class CAStoreOperation extends ArrayStoreOperation {
	
	public CAStoreOperation(DecompilationContext context) {
		super(ArrayType.CHAR_ARRAY, context);
	}
}
