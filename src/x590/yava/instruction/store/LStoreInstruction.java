package x590.yava.instruction.store;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.store.LStoreOperation;

public class LStoreInstruction extends StoreInstruction {

	private static final Int2ObjectMap<LStoreInstruction> INSTANCES = Instruction.newCache();

	private LStoreInstruction(int slot) {
		super(slot);
	}

	public static LStoreInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, LStoreInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new LStoreOperation(context, slot);
	}

	@Override
	protected String getMnemonic() {
		return "lstore";
	}
}
