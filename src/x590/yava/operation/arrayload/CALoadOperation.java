package x590.yava.operation.arrayload;

import x590.yava.context.DecompilationContext;
import x590.yava.type.reference.ArrayType;

public final class CALoadOperation extends ArrayLoadOperation {
	
	public CALoadOperation(DecompilationContext context) {
		super(ArrayType.CHAR_ARRAY, context);
	}
}
