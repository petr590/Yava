package x590.yava.instruction.constant;

import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.LongConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.LConstOperation;

public class LConstInstruction extends ConstInstruction<LongConstant> {
	
	public LConstInstruction(LongConstant constant) {
		super(constant);
	}
	
	public LConstInstruction(long value) {
		super(ConstantPool.findOrCreateConstant(value));
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new LConstOperation(constant);
	}
}
