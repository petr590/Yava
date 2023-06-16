package x590.yava.instruction.constant;

import x590.yava.constpool.ConstValueConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.DisassemblerContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.type.TypeSize;

public class LdcInstruction extends InstructionWithIndex {
	
	private final TypeSize size;
	
	public LdcInstruction(TypeSize size, DisassemblerContext context, int index) {
		super(index);
		this.size = size;
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return context.pool.<ConstValueConstant>get(index).toOperation(size);
	}
}
