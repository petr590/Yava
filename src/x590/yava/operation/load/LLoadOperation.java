package x590.yava.operation.load;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class LLoadOperation extends LoadOperation {
	
	public LLoadOperation(DecompilationContext context, int index) {
		super(PrimitiveType.LONG, context, index);
	}
}
