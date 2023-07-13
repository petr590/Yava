package x590.yava.instruction.load;

import x590.yava.instruction.binary.SimpleInstructionWithSlot;

public abstract class LoadInstruction extends SimpleInstructionWithSlot {

	public LoadInstruction(int slot) {
		super(slot);
	}
}
