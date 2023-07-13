package x590.yava.instruction.load;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.load.ILoadOperation;

public class ILoadInstruction extends LoadInstruction {

	private static final Int2ObjectMap<ILoadInstruction> INSTANCES = Instruction.newCache();

	private ILoadInstruction(int slot) {
		super(slot);
	}

	public static ILoadInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, ILoadInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new ILoadOperation(context, slot);
	}

	@Override
	public String getMnemonic() {
		return "iload";
	}
}
