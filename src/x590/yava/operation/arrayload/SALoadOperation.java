package x590.yava.operation.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class SALoadOperation extends ArrayLoadOperation {
	
	public SALoadOperation(DecompilationContext context) {
		super(ArrayType.SHORT_ARRAY, context);
	}
}
