package x590.yava.instruction.constant;

import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.FloatConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.FConstOperation;

public class FConstInstruction extends ConstInstruction<FloatConstant> {

	public FConstInstruction(FloatConstant constant) {
		super(constant);
	}

	public FConstInstruction(float value) {
		super(ConstantPool.findOrCreateConstant(value));
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new FConstOperation(constant);
	}
}
