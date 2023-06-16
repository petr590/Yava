package x590.yava.operation.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.type.Type;

public abstract class UnaryOperatorOperation extends OperatorOperation {
	
	protected final Operation operand;
	
	public UnaryOperatorOperation(Type type, DecompilationContext context) {
		super(type);
		this.operand = context.popAsNarrowest(type);
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.print(getOperator()).printPrioritied(this, operand, context, Associativity.RIGHT);
	}
	
	@Override
	public boolean requiresLocalContext() {
		return operand.requiresLocalContext();
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof UnaryOperatorOperation operation &&
				super.equals(operation) && operand.equals(operation.operand);
	}
}
