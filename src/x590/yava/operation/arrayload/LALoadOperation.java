package x590.yava.operation.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class LALoadOperation extends ArrayLoadOperation {
	
	public LALoadOperation(DecompilationContext context) {
		super(ArrayType.LONG_ARRAY, context);
	}
}
