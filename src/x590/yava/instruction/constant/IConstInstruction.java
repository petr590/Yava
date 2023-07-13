package x590.yava.instruction.constant;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.constvalue.IntegerConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.IConstOperation;

public class IConstInstruction extends ConstInstruction<IntegerConstant> {

	private static final Int2ObjectMap<IConstInstruction> INSTANCES = Instruction.newCache();

	private IConstInstruction(int value) {
		super(ConstantPool.findOrCreateConstant(value));
	}

	public static IConstInstruction of(int value) {
		return INSTANCES.computeIfAbsent(value, IConstInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new IConstOperation(constant);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		int value = constant.getValue();

		if (value >= -1 && value <= 5) {
			out.print("iconst_").printInt(value);
		} else if ((byte)value == value) {
			out.printsp("bipush").printInt(value);
		} else if ((short)value == value) {
			out.printsp("sipush").printInt(value);
		} else {
			out.printsp("iconst").printInt(value);
		}
	}
}
