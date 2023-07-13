package x590.yava.instruction.other;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.binary.SimpleInstructionWithMnemonic;
import x590.yava.operation.Operation;

import java.util.function.Function;

/**
 * Инструкция, которая содержит мнемонику и функцию для преобразования в операцию
 */
public class SimpleOperationInstruction<O extends Operation> extends SimpleInstructionWithMnemonic {

	private final Function<DecompilationContext, O> operationCreator;

	public SimpleOperationInstruction(String mnemonic, Function<DecompilationContext, O> operationCreator) {
		super(mnemonic);
		this.operationCreator = operationCreator;
	}

	@Override
	public O toOperation(DecompilationContext context) {
		return operationCreator.apply(context);
	}
}
