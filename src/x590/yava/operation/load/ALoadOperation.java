package x590.yava.operation.load;

import x590.yava.context.DecompilationContext;
import x590.yava.type.Types;

public final class ALoadOperation extends LoadOperation {

	public ALoadOperation(DecompilationContext context, int index) {
		super(Types.ANY_OBJECT_TYPE, context, index);
	}
}
