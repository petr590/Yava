package x590.yava.instruction.binary;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.constpool.Constant;
import x590.yava.constpool.InterfaceMethodrefConstant;
import x590.yava.constpool.InvokeDynamicConstant;
import x590.yava.io.DisassemblingOutputStream;

public class BinaryInstructionWithConstant<C extends Constant> extends BinaryInstructionWithIndex {

	private final Class<C> constantClass;

	public BinaryInstructionWithConstant(String mnemonic, Class<C> constantClass, int index) {
		super(mnemonic, index);
		this.constantClass = constantClass;
	}

	public static BinaryInstruction invokeInterface(int index, int count, int zeroByte) {
		return new BinaryInstructionWithConstant<>("invokeinterface", InterfaceMethodrefConstant.class, index);
	}

	public static BinaryInstruction invokeDynamic(int index, int zeroShort) {
		return new BinaryInstructionWithConstant<>("invokedynamic", InvokeDynamicConstant.class, index);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		out .printsp(mnemonic)
			.print(context.getConstPool().get(index, constantClass), context.getClassInfo());
	}
}
