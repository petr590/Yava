package x590.yava.operation.arraystore;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class AAStoreOperation extends ArrayStoreOperation {
	
	public AAStoreOperation(DecompilationContext context) {
		super(ArrayType.ANY_OBJECT_ARRAY, context);
	}
}
