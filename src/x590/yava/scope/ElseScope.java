package x590.yava.scope;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.operator.TernaryOperatorOperation;

public class ElseScope extends Scope {

	protected final IfScope ifScope;
	private @Nullable ElseIfPair elseIfPart;

	protected ElseScope(DecompilationContext context, int endIndex, IfScope ifScope) {
		super(context, endIndex, ifScope.superScope());
		this.ifScope = ifScope;
	}


	@Override
	public boolean isTerminable() {
		return this.isLastOperationTerminable() && ifScope.isLastOperationTerminable();
	}


	boolean canSelfOmitCurlyBrackets() {
		return super.canOmitCurlyBrackets() ||
				elseIfPart != null && elseIfPart.canSelfOmitCurlyBrackets();
	}

	boolean canSelfOmitCurlyBracketsForward() {
		return canSelfOmitCurlyBrackets() ||
				elseIfPart != null && elseIfPart.canSelfOmitCurlyBracketsForward();
	}

	boolean canSelfOmitCurlyBracketsBackward() {
		return canSelfOmitCurlyBrackets() && ifScope.canSelfOmitCurlyBracketsBackward();
	}

	@Override
	protected boolean canOmitCurlyBrackets() {
		return canSelfOmitCurlyBracketsForward() && ifScope.canSelfOmitCurlyBracketsBackward();
	}


	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if (elseIfPart != null) {
			writeHeader(out, context);

			out.printsp();
			elseIfPart.writeTo(out, context);

		} else {
			super.writeTo(out, context);
		}
	}

	@Override
	public void writeFront(StringifyOutputStream out, StringifyContext context) {
		if (canOmitCurlyBrackets())
			out.println().printIndent();
	}

	@Override
	protected void writeHeader(StringifyOutputStream out, StringifyContext context) {
		out.write(canOmitCurlyBrackets() ? "else" : " else");
	}

	@Override
	public void finalizeScope(DecompilationContext context) {
		super.finalizeScope(context);

		context.updateStackState();

		final int endIndex = endIndex();
		final var stackState = context.getStackState(endIndex);

		if (stackState != null && ifScope.isEmpty() && this.isEmpty() && !stackState.isEmpty() && context.stackNotEmpty()) {

			context.push(new TernaryOperatorOperation(ifScope.getCondition(), context.pop(), stackState.pop()));
			this.remove();
			ifScope.remove();

			context.pollStackState(endIndex);
			context.resetStackStateUpdated();

		} else {
			int operationsCount = getOperationsCount();

			if (operationsCount == 1 && getOperationAt(0) instanceof IfScope nextIfScope) {
				elseIfPart = new ElseIfPair(nextIfScope, null);

			} else if (operationsCount == 2 &&
					getOperationAt(0) instanceof IfScope nextIfScope &&
					getOperationAt(1) instanceof ElseScope nextElseScope) {

				elseIfPart = new ElseIfPair(nextIfScope, nextElseScope);
				nextIfScope.setPrevElse(this);
			}
		}
	}


	static class ElseIfPair {
		private final IfScope ifScope;
		private final @Nullable ElseScope elseScope;

		private ElseIfPair(IfScope ifScope, @Nullable ElseScope elseScope) {
			this.ifScope = ifScope;
			this.elseScope = elseScope;
		}

		public void writeTo(StringifyOutputStream out, StringifyContext context) {
			ifScope.writeTo(out, context);
			ifScope.writeBack(out, context);

			if (elseScope != null)
				elseScope.writeAsStatement(out, context);
		}

		public boolean canSelfOmitCurlyBrackets() {
			return ifScope.canSelfOmitCurlyBrackets() &&
					(elseScope == null || elseScope.canSelfOmitCurlyBrackets());
		}

		public boolean canSelfOmitCurlyBracketsForward() {
			return ifScope.canSelfOmitCurlyBracketsForward() &&
					(elseScope == null || elseScope.canSelfOmitCurlyBracketsForward());
		}
	}
}