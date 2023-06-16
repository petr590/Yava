package x590.yava.operation.cmp;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class LCmpOperation extends CmpOperation {
	
	public LCmpOperation(DecompilationContext context) {
		super(PrimitiveType.LONG, context);
	}
}
