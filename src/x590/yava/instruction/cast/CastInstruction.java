package x590.yava.instruction.cast;

import x590.yava.context.DecompilationContext;
import x590.yava.instruction.binary.SimpleInstructionWithMnemonic;
import x590.yava.operation.Operation;
import x590.yava.operation.cast.CastOperation;
import x590.yava.type.Type;

public class CastInstruction extends SimpleInstructionWithMnemonic {

	protected final Type requiredType, castedType;
	protected final boolean implicitCast;

	public CastInstruction(String mnemonic, Type requiredType, Type castedType, boolean implicitCast) {
		super(mnemonic);
		this.requiredType = requiredType;
		this.castedType = castedType;
		this.implicitCast = implicitCast;
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return CastOperation.of(requiredType, castedType, implicitCast, context);
	}
}
