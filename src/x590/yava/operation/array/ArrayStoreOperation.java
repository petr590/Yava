package x590.yava.operation.array;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.ReturnableOperation;
import x590.yava.operation.constant.IConstOperation;
import x590.yava.operation.increment.IncrementableOperation;
import x590.yava.type.CastingKind;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ArrayType;
import x590.yava.type.reference.IArrayType;

public class ArrayStoreOperation extends ReturnableOperation implements IncrementableOperation {

	private Operation array;
	private final Operation index;
	private Operation value;
	private IncrementData incData;


	private void castArrayType() {
		value = value.useAsNarrowest(((IArrayType) array.getReturnType()).getElementType());
		array = array.useAsWidest(value.getReturnType().arrayTypeAsType());
	}


	public ArrayStoreOperation(ArrayType requiredType, DecompilationContext context) {
		super(PrimitiveType.VOID);
		this.value = context.pop();
		this.index = context.popAsNarrowest(PrimitiveType.INT);
		this.array = context.popAsNarrowest(requiredType);

		castArrayType();

		if (array instanceof NewArrayOperation newArray &&
				index instanceof IConstOperation iconst &&
				newArray.addToInitializer(value, iconst)) {

			this.remove();
		}

		this.incData = init(context, value, value.getReturnType());
	}

	public Operation getArray() {
		return array;
	}

	public Operation getIndex() {
		return index;
	}

	public Operation getValue() {
		return value;
	}


	@Override
	protected void onCastReturnType(Type newType, CastingKind kind) {
		super.onCastReturnType(newType, kind);
		array = array.useAs(newType.arrayTypeAsType(), kind);
	}

	@Override
	protected Type getDeducedType(Type returnType) {
		castArrayType();

		var preIncLoadOperation = incData.getPreIncLoadOperation();

		return preIncLoadOperation != null ?
				preIncLoadOperation.getReturnType() :
				returnType;
	}

	@Override
	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	@Override
	public IncrementData getIncData() {
		return incData;
	}

	@Override
	public void setIncData(IncrementData incData) {
		this.incData = incData;
	}


	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		IncrementableOperation.super.writeTo(out, context, returnType, incData);
	}

	@Override
	public void writeName(StringifyOutputStream out, StringifyContext context) {
		out.printPrioritied(this, array, context, Associativity.LEFT).print('[').print(index, context).print(']');
	}

	@Override
	public void writeValue(StringifyOutputStream out, StringifyContext context) {
		out.print(value, context);
	}

	@Override
	public void allowShortArrayInitializer() {
		value.allowShortArrayInitializer();
	}


	@Override
	public boolean isLoadOperation(Operation operation) {
		return operation instanceof ArrayLoadOperation arrayLoad &&
				arrayLoad.getArray().equals(array) && arrayLoad.getIndex().equals(index);
	}


	@Override
	public boolean requiresLocalContext() {
		return !this.isRemoved() && (array.requiresLocalContext() || index.requiresLocalContext() || value.requiresLocalContext());
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof ArrayStoreOperation operation &&
				array.equals(operation.array) && index.equals(operation.index) && value.equals(operation.value);
	}
}
