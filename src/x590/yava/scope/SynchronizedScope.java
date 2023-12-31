package x590.yava.scope;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.load.ALoadOperation;
import x590.yava.operation.store.AStoreOperation;
import x590.yava.type.Types;
import x590.yava.variable.EmptyableVariable;

public class SynchronizedScope extends Scope {

	private final Operation object;
	private final EmptyableVariable variable;

	public SynchronizedScope(DecompilationContext context) {
		super(context, context.currentScope().endIndex());
		this.object = context.popAsNarrowest(Types.ANY_OBJECT_TYPE);

		if (context.currentScope().getLastOperation() instanceof AStoreOperation astoreOperation && astoreOperation.getValue() == object) {
			astoreOperation.remove();
			this.variable = astoreOperation.getVariable();
		} else {
			this.variable = null;
			context.warning("Cannot find variable for monitorenter instruction, maybe code is broken");
		}
	}

	public EmptyableVariable getVariable() {
		return variable;
	}

	@Override
	protected void writeHeader(StringifyOutputStream out, StringifyContext context) {
		out.print("synchronized(").print(object, context).print(')');
	}

	public static @Nullable Operation monitorExit(DecompilationContext context) {
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