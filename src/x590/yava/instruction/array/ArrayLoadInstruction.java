package x590.yava.instruction.array;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.other.SimpleOperationInstruction;
import x590.yava.operation.array.ArrayLoadOperation;

import java.util.function.Function;

public class ArrayLoadInstruction extends SimpleOperationInstruction<ArrayLoadOperation> {

	public ArrayLoadInstruction(String mnemonic, Function<DecompilationContext, ArrayLoadOperation> operationCreator) {
		super(mnemonic, operationCreator);
	}
}
