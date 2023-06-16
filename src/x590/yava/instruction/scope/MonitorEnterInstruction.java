package x590.yava.instruction.scope;

import x590.yava.context.DecompilationContext;
import x590.yava.scope.SynchronizedScope;
import x590.yava.scope.Scope;

public class MonitorEnterInstruction extends ScopeInstruction {
	
	@Override
	protected Scope toScope(DecompilationContext context) {
		return new SynchronizedScope(context);
	}
}
