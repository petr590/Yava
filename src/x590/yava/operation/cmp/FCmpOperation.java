package x590.yava.operation.cmp;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class FCmpOperation extends CmpOperation {
	
	public FCmpOperation(DecompilationContext context) {
		super(PrimitiveType.FLOAT, context);
	}
}
