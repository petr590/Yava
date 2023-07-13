package x590.yava.instruction.store;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.store.DStoreOperation;

public class DStoreInstruction extends StoreInstruction {

	private static final Int2ObjectMap<DStoreInstruction> INSTANCES = Instruction.newCache();

	private DStoreInstruction(int slot) {
		super(slot);
	}

	public static DStoreInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, DStoreInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new DStoreOperation(context, slot);
	}

	@Override
	protected String getMnemonic() {
		return "dstore";
	}
}
