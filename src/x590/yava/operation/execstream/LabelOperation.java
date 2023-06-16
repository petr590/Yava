package x590.yava.operation.execstream;

import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.VoidOperation;
import x590.yava.scope.Scope;

public abstract class LabelOperation extends AbstractOperation implements VoidOperation {
	
	private final Scope scope;
	private final boolean hasOtherScope;
	
	public LabelOperation(Scope scope, boolean hasOtherScope) {
		this.scope = scope;
		this.hasOtherScope = hasOtherScope;
		
		if(hasOtherScope)
			scope.initLabel();
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public boolean hasOtherScope() {
		return hasOtherScope;
	}
	
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		writeAction(out, context);
		
		if(hasOtherScope) {
			out.printsp();
			scope.writeLabel(out);
		}
	}
	
	protected abstract void writeAction(StringifyOutputStream out, StringifyContext context);
}
