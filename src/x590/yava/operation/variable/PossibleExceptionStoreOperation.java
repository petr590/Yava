package x590.yava.operation.variable;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.load.ExceptionLoadOperation;
import x590.yava.scope.CatchScope;
import x590.yava.variable.Variable;

public interface PossibleExceptionStoreOperation extends Operation {

	default boolean removeIfExceptionLoadOperation(DecompilationContext context, Operation value) {
		if (value instanceof ExceptionLoadOperation &&
				context.currentScope() instanceof CatchScope catchScope &&
				context.currentIndex() == catchScope.startIndex() + 1) {

			this.remove();
			return true;
		}

		return false;
	}

	@Nullable Variable getStoringVariable();
}
