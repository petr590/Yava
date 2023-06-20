package x590.yava.instruction.scope;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.load.ALoadOperation;
import x590.yava.scope.Scope;
import x590.yava.scope.SynchronizedScope;
import x590.yava.type.Types;
import x590.yava.variable.EmptyableVariable;

public class MonitorExitInstruction extends ScopeInstruction {

	@Override
	protected @Nullable Scope toScope(DecompilationContext context) {
		Operation value = context.popAsNarrowest(Types.ANY_OBJECT_TYPE);

		if (value instanceof ALoadOperation aloadOperation) {
			aloadOperation.remove();
			EmptyableVariable variable = aloadOperation.getVariable();

			for (Scope scope : context.getCurrentScopes()) {
				if (scope instanceof SynchronizedScope synchronizedScope && synchronizedScope.getVariable() == variable) {
					synchronizedScope.setEndIndex(context.currentIndex());
				}
			}

		} else {
			context.warning("Cannot find variable for monitorexit instruction, maybe code is broken");
		}

		return null;
	}
}
