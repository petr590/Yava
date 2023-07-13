package x590.yava.instruction.binary;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.io.DisassemblingOutputStream;

public class BinaryInstructionWithSlot extends BinaryInstructionWithIndex {

	public BinaryInstructionWithSlot(String mnemonic, int index) {
		super(mnemonic, index);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		out.printsp(mnemonic).printInt(index);
	}
}
