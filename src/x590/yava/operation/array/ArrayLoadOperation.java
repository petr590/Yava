package x590.yava.operation.array;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.ReturnableOperation;
import x590.yava.type.CastingKind;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ArrayType;
import x590.yava.type.reference.IArrayType;

public class ArrayLoadOperation extends ReturnableOperation {

	private Operation array;
	private final Operation index;

	public ArrayLoadOperation(ArrayType requiredType, DecompilationContext context) {
		super(requiredType);
		this.index = context.popAsNarrowest(PrimitiveType.INT);
		this.array = context.popAsNarrowest(requiredType);

		this.returnType = ((IArrayType) array.getReturnType()).getElementType();
	}

	public Operation getArray() {
		return array;
	}

	public Operation getIndex() {
		return index;
	}

	@Override
	public Type getDeducedType(Type returnType) {
		return ((IArrayType) array.getReturnType()).getElementType();
	}

	@Override
	protected void onCastReturnType(Type newType, CastingKind kind) {
		super.onCastReturnType(newType, kind);
		array = array.useAs(newType.arrayTypeAsType(), kind);
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.printPrioritied(this, array, context, Associativity.LEFT).print('[').print(index, context).print(']');
	}

	@Override
	public boolean requiresLocalContext() {
		return array.requiresLocalContext() || index.requiresLocalContext();
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof ArrayLoadOperation operation &&
				array.equals(operation.array) && index.equals(operation.index);
	}
}
