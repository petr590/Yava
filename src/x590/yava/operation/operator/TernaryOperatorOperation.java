package x590.yava.operation.operator;

import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.operation.ReturnableOperation;
import x590.yava.operation.condition.ConditionOperation;
import x590.yava.operation.constant.IConstOperation;
import x590.yava.type.CastingKind;
import x590.yava.type.GeneralCastingKind;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

public final class TernaryOperatorOperation extends ReturnableOperation {

	private final ConditionOperation condition;
	private Operation operand1, operand2;

	private Type getGeneralType(Operation operand1, Operation operand2) {
		return getReturnTypeAsGeneralNarrowest(operand1, operand2, GeneralCastingKind.TERNARY_OPERATOR);
	}

	public TernaryOperatorOperation(ConditionOperation condition, Operation operand1, Operation operand2) {
		super(operand1.getReturnType().castToGeneral(operand2.getReturnType(), GeneralCastingKind.TERNARY_OPERATOR));
		this.condition = condition;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	@Override
	protected Type getDeducedType(Type returnType) {
		return getGeneralType(operand1, operand2);
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if (isBooleanCondition()) {
			out.print(((IConstOperation)operand1).getValue() != 0 ? condition : condition.invert(), context);
		} else {
			out.print(condition, context).print(" ? ")
					.printPrioritied(this, operand1, context, Associativity.LEFT).print(" : ")
					.printPrioritied(this, operand2, context, Associativity.RIGHT);
		}
	}

	private boolean isBooleanCondition() {
		return returnType.canCastToNarrowest(PrimitiveType.BOOLEAN) &&
				operand1 instanceof IConstOperation iconst1 &&
				operand2 instanceof IConstOperation iconst2 &&
				(iconst1.getValue() == 1 && iconst2.getValue() == 0 ||
				 iconst1.getValue() == 0 && iconst2.getValue() == 1);
	}

	@Override
	public int getPriority() {
		return isBooleanCondition() ?
				condition.getPriority() :
				Priority.TERNARY_OPERATOR;
	}

	@Override
	public void onCastReturnType(Type newType, CastingKind kind) {
		super.onCastReturnType(newType, kind);
		operand1 = operand1.useAs(returnType, kind);
		operand2 = operand2.useAs(returnType, kind);
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof TernaryOperatorOperation operation &&
				super.equals(operation) && condition.equals(operation.condition) &&
				operand1.equals(operation.operand1) && operand2.equals(operation.operand2);
	}
}
