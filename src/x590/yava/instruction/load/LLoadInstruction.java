package x590.yava.instruction.load;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.load.LLoadOperation;

public class LLoadInstruction extends LoadInstruction {

	private static final Int2ObjectMap<LLoadInstruction> INSTANCES = Instruction.newCache();

	private LLoadInstruction(int slot) {
		super(slot);
	}

	public static LLoadInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, LLoadInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new LLoadOperation(context, slot);
	}

	@Override
	public String getMnemonic() {
		return "lload";
	}
}
