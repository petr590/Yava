package x590.yava.instruction;

public abstract class InstructionWithIndex implements Instruction {

	protected final int index;

	public InstructionWithIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
