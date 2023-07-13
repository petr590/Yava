package x590.yava.instruction.array;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.other.SimpleOperationInstruction;
import x590.yava.operation.array.ArrayStoreOperation;

import java.util.function.Function;

public class ArrayStoreInstruction extends SimpleOperationInstruction<ArrayStoreOperation> {

	public ArrayStoreInstruction(String mnemonic, Function<DecompilationContext, ArrayStoreOperation> operationCreator) {
		super(mnemonic, operationCreator);
	}
}
