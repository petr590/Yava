package x590.yava.instruction.cast;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.InstructionWithIndex;
import x590.yava.operation.Operation;
import x590.yava.operation.cast.CastOperation;

public class CheckCastInstruction extends InstructionWithIndex {
	
	public CheckCastInstruction(int index) {
		super(index);
	}
	
	@Override
	public Operation toOperation(DecompilationContext context) {
		return CastOperation.objectCast(context, index);
	}
}
