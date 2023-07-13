package x590.yava.instruction.binary;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.instruction.array.NewArrayInstruction;
import x590.yava.io.DisassemblingOutputStream;

public class BinaryNewArrayInstruction extends BinaryInstructionWithIndex {

	public BinaryNewArrayInstruction(String mnemonic, int index) {
		super(mnemonic, index);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		out.printsp(mnemonic).print(NewArrayInstruction.getArrayTypeByCode(index), context.getClassInfo());
	}
}
