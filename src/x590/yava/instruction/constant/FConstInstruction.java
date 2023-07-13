package x590.yava.instruction.constant;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.constvalue.FloatConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.FConstOperation;

public class FConstInstruction extends ConstInstruction<FloatConstant> {

	public FConstInstruction(FloatConstant constant) {
		super(constant);
	}

	public FConstInstruction(float value) {
		super(ConstantPool.findOrCreateConstant(value));
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new FConstOperation(constant);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		float value = constant.getValue();

		out .print("fconst")
			.print(value == 0 || value == 1 || value == 2 ? '_' : ' ')
			.printInt((int)value);
	}
}
