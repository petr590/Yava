package x590.yava.operation.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.operation.VoidOperation;
import x590.yava.operation.operator.TernaryOperatorOperation;
import x590.yava.scope.IfScope;
import x590.yava.type.Type;

import java.util.function.Predicate;

public abstract class ReturnOperation extends AbstractOperation implements VoidOperation {

	private final Operation operand;


	public ReturnOperation(Type requiredType, DecompilationContext context) {
		this(methodReturnType -> methodReturnType.canCastToNarrowest(requiredType), context);
	}

	public ReturnOperation(Predicate<Type> predicate, DecompilationContext context) {

		Type methodReturnType = context.getGenericDescriptor().getReturnType();

		if (!predicate.test(methodReturnType))
			throw new DecompilationException("The method return type (" + methodReturnType + ")" +
					" does not match type of the `" + getInstructionName() + "` instruction");

		Operation operand = context.popAsNarrowest(methodReturnType);

		if (context.currentScope().getLastOperation() instanceof IfScope ifScope &&
				ifScope.getOperationsCount() == 1 && ifScope.getOperationAt(0) instanceof ReturnOperation returnOperation) {

			operand = new TernaryOperatorOperation(ifScope.getCondition(), returnOperation.operand, operand);
			ifScope.remove();
		}

		this.operand = operand;
		operand.allowImplicitCast();
	}


	public Operation getOperand() {
		return operand;
	}

	protected abstract String getInstructionName();


	@Override
	public boolean isTerminable() {
		return true;
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.printsp("return").print(operand, context);
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || this.getClass() == other.getClass() &&
				operand.equals(((ReturnOperation)other).operand);
	}
}
