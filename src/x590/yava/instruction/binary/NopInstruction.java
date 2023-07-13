package x590.yava.instruction.binary;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.clazz.ClassInfo;
import x590.yava.io.DisassemblingOutputStream;

public final class NopInstruction implements BinaryInstruction {

	public static NopInstruction INSTANCE = new NopInstruction();

	private NopInstruction() {}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		out.write("nop");
	}
}
