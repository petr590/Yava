package x590.yava.instruction.increment;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.instruction.binary.SimpleInstructionWithSlot;
import x590.yava.operation.Operation;
import x590.yava.operation.increment.IIncOperation;

public class IIncInstruction extends SimpleInstructionWithSlot {

	private final int value;

	public IIncInstruction(int slot, int value) {
		super(slot);
		this.value = value;
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new IIncOperation(context, slot, value);
	}

	@Override
	protected String getMnemonic() {
		return "iinc";
	}
}
