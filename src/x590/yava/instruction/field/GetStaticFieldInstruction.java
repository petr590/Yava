package x590.yava.instruction.field;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.operation.field.GetStaticFieldOperation;

public class GetStaticFieldInstruction extends InstructionWithIndex {
	
	public GetStaticFieldInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new GetStaticFieldOperation(context, index);
	}
}
