package x590.yava.instruction.binary;

public class BinaryInstructionWithIndex extends BinaryInstructionWithMnemonic {

	protected final int index;

	public BinaryInstructionWithIndex(String mnemonic, int index) {
		super(mnemonic);
		this.index = index;
	}
}
