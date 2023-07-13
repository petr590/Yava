package x590.yava.instruction.binary;

import x590.yava.instruction.InstructionWithIndex;

public abstract class SimpleInstructionWithIndex extends InstructionWithIndex implements SimpleInstruction {
	public SimpleInstructionWithIndex(int index) {
		super(index);
	}
}
