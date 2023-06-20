package x590.yava.instruction.other;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.operation.other.InstanceofOperation;

public class InstanceofInstruction extends InstructionWithIndex {

	public InstanceofInstruction(int index) {
		super(index);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new InstanceofOperation(context, index);
	}
}
