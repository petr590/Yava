package x590.yava.instruction.store;

import x590.yava.instruction.binary.SimpleInstructionWithSlot;

public abstract class StoreInstruction extends SimpleInstructionWithSlot {

	public StoreInstruction(int slot) {
		super(slot);
	}
}
