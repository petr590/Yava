package x590.yava.scope;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.load.ALoadOperation;
import x590.yava.operation.other.AThrowOperation;

import java.util.Collections;

public class FinallyScope extends CatchScope {

	public FinallyScope(DecompilationContext context, int endIndex, boolean hasNext) {
		super(context, endIndex, Collections.emptyList(), hasNext);
	}

	@Override
	public void addOperation(Operation operation, int fromIndex) {
		// Не добавлять {@code throw ex;} в конец finally

		if (operation instanceof AThrowOperation athrow &&
				athrow.getOperand() instanceof ALoadOperation aload &&
				aload.getVariable() == getLoadOperation().getVariable()) {

			setEndIndex(fromIndex);

		} else {
			super.addOperation(operation, fromIndex);
		}
	}

	@Override
	protected void writeHeader(StringifyOutputStream out, StringifyContext context) {
		out.write("finally");
	}
}
