package x590.yava.instruction.load;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.load.ALoadOperation;

public class ALoadInstruction extends LoadInstruction {

	private static final Int2ObjectMap<ALoadInstruction> INSTANCES = Instruction.newCache();

	private ALoadInstruction(int slot) {
		super(slot);
	}

	public static ALoadInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, ALoadInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new ALoadOperation(context, slot);
	}

	@Override
	protected String getMnemonic() {
		return "aload";
	}
}
