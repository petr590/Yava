package x590.yava.instruction.increment;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.operation.increment.IIncOperation;

public class IIncInstruction extends InstructionWithIndex {

	private final int value;

	public IIncInstruction(int index, int value) {
		super(index);
		this.value = value;
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new IIncOperation(context, index, value);
	}
}
