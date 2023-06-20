package x590.yava.instruction.scope;

import x590.yava.context.DecompilationContext;
import x590.yava.scope.Scope;
import x590.yava.scope.SynchronizedScope;

public class MonitorEnterInstruction extends ScopeInstruction {

	@Override
	protected Scope toScope(DecompilationContext context) {
		return new SynchronizedScope(context);
	}
}
