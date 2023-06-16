package x590.yava.operation.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class AALoadOperation extends ArrayLoadOperation {
	
	public AALoadOperation(DecompilationContext context) {
		super(ArrayType.ANY_OBJECT_ARRAY, context);
	}
}
