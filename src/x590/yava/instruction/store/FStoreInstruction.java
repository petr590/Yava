package x590.yava.instruction.store;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.store.FStoreOperation;

public class FStoreInstruction extends StoreInstruction {

	private static final Int2ObjectMap<FStoreInstruction> INSTANCES = Instruction.newCache();

	private FStoreInstruction(int slot) {
		super(slot);
	}

	public static FStoreInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, FStoreInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new FStoreOperation(context, slot);
	}

	@Override
	protected String getMnemonic() {
		return "fstore";
	}
}
