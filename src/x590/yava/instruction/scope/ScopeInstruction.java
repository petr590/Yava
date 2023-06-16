package x590.yava.instruction.scope;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.scope.Scope;
import x590.util.annotation.Nullable;

public abstract class ScopeInstruction implements Instruction {
	
	@Override
	public @Nullable Operation toOperation(DecompilationContext context) {
		return toScope(context);
	}
	
	protected abstract @Nullable Scope toScope(DecompilationContext context);
}
