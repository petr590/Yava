package x590.yava.instruction.constant;

import x590.util.annotation.Nullable;
import x590.yava.constpool.ConstantPool;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.DConstOperation;

public class DPushInstruction implements Instruction {

	protected final double value;

	public DPushInstruction(double value) {
		this.value = value;
	}

	@Override
	public @Nullable Operation toOperation(DecompilationContext context) {
		return new DConstOperation(ConstantPool.findOrCreateConstant(value));
	}
}
