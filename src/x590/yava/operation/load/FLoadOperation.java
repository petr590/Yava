package x590.yava.operation.load;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class FLoadOperation extends LoadOperation {
	
	public FLoadOperation(DecompilationContext context, int index) {
		super(PrimitiveType.FLOAT, context, index);
	}
}
