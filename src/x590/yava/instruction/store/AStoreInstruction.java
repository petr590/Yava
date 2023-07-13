package x590.yava.instruction.store;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.store.AStoreOperation;

public class AStoreInstruction extends StoreInstruction {

	private static final Int2ObjectMap<AStoreInstruction> INSTANCES = Instruction.newCache();

	private AStoreInstruction(int slot) {
		super(slot);
	}

	public static AStoreInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, AStoreInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new AStoreOperation(context, slot);
	}

	@Override
	protected String getMnemonic() {
		return "astore";
	}
}
