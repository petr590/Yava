package x590.yava.instruction.binary;

/**
 * Инструкция, которая содержит мнемонику
 */
public abstract class SimpleInstructionWithMnemonic extends BinaryInstructionWithMnemonic implements SimpleInstruction {

	public SimpleInstructionWithMnemonic(String mnemonic) {
		super(mnemonic);
	}
}
