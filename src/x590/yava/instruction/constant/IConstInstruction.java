package x590.yava.instruction.constant;

import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.IntegerConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.IConstOperation;

public class IConstInstruction extends ConstInstruction<IntegerConstant> {

	public IConstInstruction(IntegerConstant constant) {
		super(constant);
	}

	public IConstInstruction(int value) {
		super(ConstantPool.findOrCreateConstant(value));
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new IConstOperation(constant);
	}
}
