package x590.yava.instruction.other;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.binary.SimpleInstructionWithMnemonic;
import x590.yava.operation.Operation;

import java.util.function.Consumer;

/**
 * Инструкция, Которая не преобразуется в операцию, а изменяет состояние стека
 */
public class StackChangingInstruction extends SimpleInstructionWithMnemonic {

	private final Consumer<DecompilationContext> duplicater;

	public StackChangingInstruction(String mnemonic, Consumer<DecompilationContext> duplicater) {
		super(mnemonic);
		this.duplicater = duplicater;
	}

	@Override
	public final Operation toOperation(DecompilationContext context) {
		duplicater.accept(context);
		return null;
	}
}