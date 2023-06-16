package x590.yava.operation.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class BALoadOperation extends ArrayLoadOperation {
	
	public BALoadOperation(DecompilationContext context) {
		super(ArrayType.BYTE_OR_BOOLEAN_ARRAY, context);
	}
}
