package x590.yava.operation.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class DALoadOperation extends ArrayLoadOperation {

	public DALoadOperation(DecompilationContext context) {
		super(ArrayType.DOUBLE_ARRAY, context);
	}
}
