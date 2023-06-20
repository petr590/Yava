package x590.yava.operation.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class FALoadOperation extends ArrayLoadOperation {

	public FALoadOperation(DecompilationContext context) {
		super(ArrayType.FLOAT_ARRAY, context);
	}
}
