package x590.yava.operation.increment;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.operation.load.LoadOperation;
import x590.yava.operation.variable.OperationWithVariable;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

public final class IIncOperation extends OperationWithVariable {

	private final int value;
	private boolean isPreInc;

	public IIncOperation(DecompilationContext context, int slot, int value) {
		super(context.currentScope().getDefinedVariable(slot));

		this.value = value;

		variable.castTypeToWidest(PrimitiveType.INT);

		if (!context.hasBreakAtCurrentIndex()) {

			if (context.stackNotEmpty() && tryLoadSameVariable(context.peek())) {
				context.pop();

			} else if (value == 1 || value == -1) {
				context.onNextPush(operation -> {
					if (tryLoadSameVariable(operation)) {
						isPreInc = true;
						context.push(this);
						context.currentScope().remove(this);
						return false;
					}

					return true;
				});
			}
		}
	}

	private boolean tryLoadSameVariable(Operation operation) {
		if (operation instanceof LoadOperation loadOperation && loadOperation.getVariable().equals(variable)) {
			returnType = variable.getType();
			return true;
		}

		return false;
	}


	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if (isPreInc) {
			out.write(value > 0 ? "++" : "--");
		}

		out.write(variable.getName());

		if (!isPreInc) {
			out.write(value < 0 ?
					value == -1 ? "--" : " -= " + value :
					value == 1 ? "++" : " += " + value
			);
		}
	}

	@Override
	protected Type getDeducedType(Type returnType) {
		return returnType == PrimitiveType.VOID ? returnType : variable.getType();
	}

	@Override
	public int getPriority() {
		return Priority.POST_INCREMENT;
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof IIncOperation operation &&
				super.equals(operation) && value == operation.value;
	}
}