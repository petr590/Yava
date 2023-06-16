package x590.yava.instruction.field;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.operation.field.GetInstanceFieldOperation;

public class GetInstanceFieldInstruction extends InstructionWithIndex {
	
	public GetInstanceFieldInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return new GetInstanceFieldOperation(context, index);
	}
}
