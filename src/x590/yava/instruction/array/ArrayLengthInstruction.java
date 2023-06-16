package x590.yava.instruction.array;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.array.ArrayLengthOperation;

public class ArrayLengthInstruction implements Instruction {
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new ArrayLengthOperation(context);
	}
}
