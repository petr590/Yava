package x590.yava.operation.load;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class ILoadOperation extends LoadOperation {

	public ILoadOperation(DecompilationContext context, int index) {
		super(PrimitiveType.BYTE_SHORT_INT_CHAR_BOOLEAN, context, index);
	}
}
