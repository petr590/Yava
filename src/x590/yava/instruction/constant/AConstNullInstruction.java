package x590.yava.instruction.constant;

import x590.util.annotation.Nullable;
import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.binary.SimpleInstruction;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.AConstNullOperation;

public final class AConstNullInstruction implements SimpleInstruction {

	public static final AConstNullInstruction INSTANCE = new AConstNullInstruction();

	private AConstNullInstruction() {}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		out.write("aconst_null");
	}

	@Override
	public @Nullable Operation toOperation(DecompilationContext context) {
		return AConstNullOperation.INSTANCE;
	}
}
