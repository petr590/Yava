package x590.yava.instruction.other;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.binary.SimpleInstructionWithMnemonic;
import x590.yava.operation.Operation;
import x590.yava.operation.other.PopOperation;
import x590.yava.type.TypeSize;

public class PopInstruction extends SimpleInstructionWithMnemonic {

	private final TypeSize size;

	public PopInstruction(String mnemonic, TypeSize size) {
		super(mnemonic);
		this.size = size;
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new PopOperation(size, context);
	}
}
