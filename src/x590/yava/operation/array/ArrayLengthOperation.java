package x590.yava.operation.array;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.IntOperation;
import x590.yava.operation.Operation;
import x590.yava.type.reference.ArrayType;

public final class ArrayLengthOperation extends AbstractOperation implements IntOperation {

	private final Operation array;

	public ArrayLengthOperation(DecompilationContext context) {
		this.array = context.popAsNarrowest(ArrayType.ANY_ARRAY);
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.printPrioritied(this, array, context, Associativity.LEFT).print(".length");
	}

	@Override
	public String getPossibleVariableName() {
		String arrayName = array.getPossibleVariableName();
		return arrayName == null ? "length" : arrayName + "Length";
	}

	@Override
	public boolean requiresLocalContext() {
		return array.requiresLocalContext();
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof ArrayLengthOperation operation &&
				array.equals(operation.array);
	}
}
