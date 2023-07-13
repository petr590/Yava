package x590.yava.instruction.binary;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.io.DisassemblingOutputStream;

public class BinaryInstructionWithLabel extends BinaryInstructionWithMnemonic {

	protected final int pos;

	public BinaryInstructionWithLabel(String mnemonic, BytecodeDisassemblingContext context, int offset) {
		super(mnemonic);
		this.pos = context.currentPos() + offset;
		context.declareLabelAt(pos);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		out.printsp(mnemonic).print(context.labelAt(pos));
	}
}
