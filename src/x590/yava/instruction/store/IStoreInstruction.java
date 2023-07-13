package x590.yava.instruction.store;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.store.IStoreOperation;

public class IStoreInstruction extends StoreInstruction {

	private static final Int2ObjectMap<IStoreInstruction> INSTANCES = Instruction.newCache();

	private IStoreInstruction(int slot) {
		super(slot);
	}

	public static IStoreInstruction of(int slot) {
		return INSTANCES.computeIfAbsent(slot, IStoreInstruction::new);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new IStoreOperation(context, slot);
	}

	@Override
	public String getMnemonic() {
		return "istore";
	}
}
