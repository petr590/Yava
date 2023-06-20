package x590.yava.operation.cmp;

import x590.yava.context.DecompilationContext;
import x590.yava.type.primitive.PrimitiveType;

public final class DCmpOperation extends CmpOperation {

	public DCmpOperation(DecompilationContext context) {
		super(PrimitiveType.DOUBLE, context);
	}
}
