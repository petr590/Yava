package x590.yava.instruction.binary;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.clazz.ClassInfo;
import x590.yava.io.DisassemblingOutputStream;

/**
 * Бинарная инструкция, которая содержит мнемонику
 */
public abstract class BinaryInstructionWithMnemonic implements BinaryInstruction {

	protected final String mnemonic;

	public BinaryInstructionWithMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		out.write(mnemonic);
	}
}
