package x590.yava.operation.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class IALoadOperation extends ArrayLoadOperation {
	
	public IALoadOperation(DecompilationContext context) {
		super(ArrayType.INT_ARRAY, context);
	}
}
