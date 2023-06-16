package x590.yava.operationinstruction;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.Instruction;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;

/**
 * Класс, описывающий объект, который является одновременно и операцией, и инструкцией.
 */
public abstract class OperationInstruction extends AbstractOperation implements Instruction {
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return this;
	}
	
	// Одинаковые методы из Instruction и Operation требуют перезаписи
	@Override
	public void postDecompilation(DecompilationContext context) {}
}
