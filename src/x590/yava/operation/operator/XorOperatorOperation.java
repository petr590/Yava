package x590.yava.operation.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.operation.constant.IConstOperation;
import x590.yava.operation.constant.LConstOperation;
import x590.yava.type.Type;

public final class XorOperatorOperation extends BitwiseOperatorOperation {

	private final boolean isBitNot;

	public XorOperatorOperation(Type type, DecompilationContext context) {
		super(type, context);
		this.isBitNot =
				operand2() instanceof IConstOperation iconst && iconst.getValue() == -1 ||
						operand2() instanceof LConstOperation lconst && lconst.getValue() == -1;
	}


	public boolean isBitNot() {
		return isBitNot;
	}


	@Override
	public String getOperator() {
		return "^";
	}


	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if (isBitNot)
			out.print('~').printPrioritied(this, operand1(), context, Associativity.RIGHT);
		else
			super.writeTo(out, context);
	}


	@Override
	public int getPriority() {
		return isBitNot ? Priority.UNARY : Priority.BIT_XOR;
	}

	@Override
	public int getVisiblePriority(Operation operand) {
		return isBitNot ? Priority.UNARY : super.getVisiblePriority(operand);
	}
}
