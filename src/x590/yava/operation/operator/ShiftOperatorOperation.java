package x590.yava.operation.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.operation.cast.CastOperation;
import x590.yava.type.CastingKind;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

public abstract class ShiftOperatorOperation extends BinaryOperatorOperation {

	public ShiftOperatorOperation(Type type, DecompilationContext context) {
		super(type, PrimitiveType.INT, context);
	}

	@Override
	protected Operation processOperand2(Operation operand2) {
		if (operand2 instanceof CastOperation cast && cast.getRequiredType() == PrimitiveType.LONG && cast.getCastedType() == PrimitiveType.INT) {
			return cast.getOperand();
		}

		return operand2;
	}

	@Override
	protected Type getDeducedType(Type returnType) {
		return returnType;
	}

	@Override
	public int getPriority() {
		return Priority.SHIFT;
	}

	@Override
	public void onCastReturnType(Type newType, CastingKind kind) {
		superOnCastReturnType(newType, kind);
		operand1(operand1().useAs(newType, kind));
	}
}
