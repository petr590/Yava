package x590.yava.operation.array;

import x590.yava.context.DecompilationContext;

public final class ANewArrayOperation extends NewArrayOperation {
	
	public ANewArrayOperation(DecompilationContext context, int index) {
		super(context, context.pool.getClassConstant(index).toReferenceType().arrayType());
	}
}
