package x590.yava.instruction.constant;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.constvalue.LongConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.LConstOperation;

public class LConstInstruction extends ConstInstruction<LongConstant> {

	public LConstInstruction(LongConstant constant) {
		super(constant);
	}

	public LConstInstruction(long value) {
		super(ConstantPool.findOrCreateConstant(value));
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new LConstOperation(constant);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		long value = constant.getValue();

		out .print("lconst")
			.print(value == 0 || value == 1 ? '_' : ' ')
			.printLong(value);
	}
}
