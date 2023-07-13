package x590.yava.instruction.load;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.load.DLoadOperation;

public class DLoadInstruction extends LoadInstruction {

	private static final Int2ObjectMap<DLoadInstruction> INSTANCES = Instruction.newCache();

	private DLoadInstruction(int slot) {
		super(slot);
	}

	public static DLoadInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, DLoadInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new DLoadOperation(context, slot);
	}

	@Override
	public String getMnemonic() {
		return "dload";
	}
}
