package x590.yava.instruction.other;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.other.AThrowOperation;

public class AThrowInstruction implements Instruction {

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new AThrowOperation(context);
	}
}
