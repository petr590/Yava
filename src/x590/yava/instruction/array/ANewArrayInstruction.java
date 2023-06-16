package x590.yava.instruction.array;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.operation.array.ANewArrayOperation;

public class ANewArrayInstruction extends InstructionWithIndex {
	
	public ANewArrayInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new ANewArrayOperation(context, index);
	}
}
