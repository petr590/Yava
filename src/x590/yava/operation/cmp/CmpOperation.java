package x590.yava.operation.cmp;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.BooleanOperation;
import x590.yava.operation.Operation;
import x590.yava.type.Type;

public abstract class CmpOperation extends AbstractOperation implements BooleanOperation {

	public final Operation operand1, operand2;

	public CmpOperation(Type requiredType, DecompilationContext context) {
		this.operand2 = context.popAsNarrowest(requiredType);
		this.operand1 = context.popAsNarrowest(requiredType);
	}

	@Override
	public final void writeTo(StringifyOutputStream out, StringifyContext context) {
		throw new UnsupportedOperationException("Method writeTo(StringifyOutputStream, StringifyContext) must not be called for CmpOperation");
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof CmpOperation operation &&
				operand1.equals(operation.operand1) && operand2.equals(operation.operand2);
	}
}