package x590.yava.scope;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;

public class TryScope extends Scope {
	
	public TryScope(DecompilationContext context, int endIndex) {
		super(context, context.currentIndex() - 1, endIndex);
	}
	
	@Override
	protected boolean canOmitCurlyBrackets() {
		return false;
	}
	
	@Override
	protected void writeHeader(StringifyOutputStream out, StringifyContext context) {
		out.write("try");
	}
	
	@Override
	public void writeSeparator(StringifyOutputStream out, StringifyContext context, Operation nextOperation) {}
}
