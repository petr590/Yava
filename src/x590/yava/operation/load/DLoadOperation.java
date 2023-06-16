package x590.yava.operation.load;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class DLoadOperation extends LoadOperation {
	
	public DLoadOperation(DecompilationContext context, int index) {
		super(PrimitiveType.DOUBLE, context, index);
	}
}
