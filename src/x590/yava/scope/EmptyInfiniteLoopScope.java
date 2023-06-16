package x590.yava.scope;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.main.Yava;

public class EmptyInfiniteLoopScope extends Scope {
	
	public EmptyInfiniteLoopScope(DecompilationContext context) {
		super(context, context.currentIndex());
	}
	
	@Override
	public boolean isTerminable() {
		return true;
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.write(Yava.getConfig().canOmitCurlyBrackets() ? "for(;;);" : "for(;;) {}");
	}
}
