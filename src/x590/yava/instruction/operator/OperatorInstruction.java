package x590.yava.instruction.operator;

import x590.yava.instruction.binary.SimpleInstructionWithMnemonic;
import x590.yava.type.Type;

public abstract class OperatorInstruction extends SimpleInstructionWithMnemonic {

	protected final Type type;

	public OperatorInstruction(String mnemonic, Type type) {
		super(mnemonic);
		this.type = type;
	}
}
