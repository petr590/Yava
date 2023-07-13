package x590.yava.instruction.binary;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.io.DisassemblingOutputStream;

public abstract class SimpleInstructionWithSlot implements SimpleInstruction {

	protected final int slot;

	public SimpleInstructionWithSlot(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		var slot = this.slot;
		out.print(getMnemonic()).print(slot >= 0 && slot <= 4 ? '_' : ' ').printInt(slot);
	}

	protected abstract String getMnemonic();
}
