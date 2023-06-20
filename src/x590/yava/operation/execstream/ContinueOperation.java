package x590.yava.operation.execstream;

import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.scope.Scope;

public class ContinueOperation extends LabelOperation {

	public ContinueOperation(Scope scope, boolean hasOtherScope) {
		super(scope, hasOtherScope);
	}


	@Override
	protected void writeAction(StringifyOutputStream out, StringifyContext context) {
		out.write("continue");
	}


	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof ContinueOperation breakOperation && this.equals(breakOperation);
	}

	public boolean equals(ContinueOperation other) {
		return this == other ||
				getScope().equals(other.getScope()) &&
						hasOtherScope() == other.hasOtherScope();
	}
}
