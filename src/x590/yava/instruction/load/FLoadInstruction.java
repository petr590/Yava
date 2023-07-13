package x590.yava.instruction.load;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.load.FLoadOperation;

public class FLoadInstruction extends LoadInstruction {

	private static final Int2ObjectMap<FLoadInstruction> INSTANCES = Instruction.newCache();

	private FLoadInstruction(int slot) {
		super(slot);
	}

	public static FLoadInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, FLoadInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new FLoadOperation(context, slot);
	}

	@Override
	public String getMnemonic() {
		return "fload";
	}
}
