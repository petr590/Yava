package x590.yava.instruction.other;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.type.TypeSize;

public class SwapInstruction implements Instruction {

	@Override
	public @Nullable Operation toOperation(DecompilationContext context) {
		Operation value1 = context.popWithSize(TypeSize.WORD);
		Operation value2 = context.popWithSize(TypeSize.WORD);

		context.push(value1);
		context.push(value2);

		return null;
	}
}
