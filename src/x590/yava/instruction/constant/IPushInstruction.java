package x590.yava.instruction.constant;

import x590.yava.constpool.ConstantPool;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.IConstOperation;
import x590.util.annotation.Nullable;

public class IPushInstruction implements Instruction {
	
	protected final int value;
	
	public IPushInstruction(int value) {
		this.value = value;
	}

	@Override
	public @Nullable Operation toOperation(DecompilationContext context) {
		return new IConstOperation(ConstantPool.findOrCreateConstant(value));
	}
}
