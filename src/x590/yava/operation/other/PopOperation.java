package x590.yava.operation.other;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.operation.VoidOperation;
import x590.yava.operation.variable.PossibleExceptionStoreOperation;
import x590.yava.type.TypeSize;
import x590.yava.variable.Variable;

public final class PopOperation extends AbstractOperation implements VoidOperation, PossibleExceptionStoreOperation {

	public final Operation operand;

	public PopOperation(TypeSize size, DecompilationContext context) {
		this.operand = context.popWithSize(size);
		removeIfExceptionLoadOperation(context, operand);
	}


	public Operation getOperand() {
		return operand;
	}


	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		operand.writeTo(out, context);
	}

	@Override
	public boolean equals(Operation other) {
		return this == other ||
				other instanceof PopOperation operation &&
						operand.equals(operation.operand);
	}

	@Override
	public @Nullable Variable getStoringVariable() {
		return null;
	}
}
