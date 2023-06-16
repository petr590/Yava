package x590.yava.operation.operator;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.main.Yava;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.type.Type;

public abstract class BitwiseOperatorOperation extends BinaryOperatorOperation {
	
	public BitwiseOperatorOperation(Type type, DecompilationContext context) {
		super(type, context);
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if(Yava.getConfig().printBracketsAroundBitwiseOperands()) {
			out.printPrioritied(this, operand1(), context, getVisiblePriority(operand1()), Associativity.LEFT)
				.printsp().print(getOperator()).printsp()
				.printPrioritied(this, operand2(), context, getVisiblePriority(operand2()), Associativity.RIGHT);
		} else {
			super.writeTo(out, context);
		}
	}
	
	public int getVisiblePriority(Operation operand) {
		return operand.getPriority() > Priority.BIT_AND ?
				Priority.BITWISE_OPERAND : this.getPriority();
	}
}
