package x590.yava.instruction.array;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.operation.array.MultiANewArrayOperation;

public class MultiANewArrayInstruction extends InstructionWithIndex {

	private final int dimensions;

	public MultiANewArrayInstruction(int index, int dimensions) {
		super(index);
		this.dimensions = dimensions;
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new MultiANewArrayOperation(context, index, dimensions);
	}
}
