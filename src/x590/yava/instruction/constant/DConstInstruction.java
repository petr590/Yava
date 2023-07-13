package x590.yava.instruction.constant;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.constvalue.DoubleConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.DConstOperation;

public class DConstInstruction extends ConstInstruction<DoubleConstant> {

	public DConstInstruction(DoubleConstant constant) {
		super(constant);
	}

	public DConstInstruction(double value) {
		super(ConstantPool.findOrCreateConstant(value));
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new DConstOperation(constant);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		double value = constant.getValue();

		out .print("dconst")
			.print(value == 0 || value == 1 ? '_' : ' ')
			.printInt((int)value);
	}
}
