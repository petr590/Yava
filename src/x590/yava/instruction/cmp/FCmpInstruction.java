package x590.yava.instruction.cmp;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.cmp.FCmpOperation;

public class FCmpInstruction implements Instruction {

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new FCmpOperation(context);
	}
}
