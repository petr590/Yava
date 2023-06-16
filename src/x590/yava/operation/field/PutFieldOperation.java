package x590.yava.operation.field;

import x590.yava.constpool.FieldrefConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.field.FieldDescriptor;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.increment.IncrementableOperation;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.util.annotation.Nullable;

public abstract class PutFieldOperation extends FieldOperation implements IncrementableOperation {
	
	private Operation value;
	private Type returnType = PrimitiveType.VOID;
	private IncrementData incData;
	
	private Operation getValue(DecompilationContext context) {
		var genericDescriptor = getGenericDescriptor();

		Operation value = context.popAsNarrowest(genericDescriptor.getType());
		value.addPossibleVariableName(genericDescriptor.getName());
		return value;
	}
	
	public PutFieldOperation(DecompilationContext context, int index) {
		super(context, index);
		this.value = getValue(context);
	}

	public PutFieldOperation(DecompilationContext context, FieldrefConstant fieldref) {
		super(context, fieldref);
		this.value = getValue(context);
	}

	public PutFieldOperation(DecompilationContext context, FieldDescriptor descriptor) {
		super(context, descriptor);
		this.value = getValue(context);
	}


	@Override
	protected void initGenericDescriptor(@Nullable Operation object) {
		super.initGenericDescriptor(object);

		var genericDescriptor = getGenericDescriptor();
		value = value.useAsNarrowest(genericDescriptor.getType());
		value.allowImplicitCast();
	}

	
	public Operation getValue() {
		return value;
	}
	
	// Мы должны вызвать этот код только после popObject, поэтому он вызывается в дочернем инициализаторе
	protected void initIncData(DecompilationContext context) {
		this.incData = IncrementableOperation.super.init(context, value, getDescriptor().getType());
	}
	
	
	@Override
	public boolean isLoadOperation(Operation operation) {
		return operation instanceof GetFieldOperation getFieldOperation &&
				getFieldOperation.getDescriptor().equals(getDescriptor());
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
		writeTo(out, context, returnType, incData);
	}
	
	@Override
	public void writeValue(StringifyOutputStream out, StringifyContext context) {
		out.print(value, context);
	}
	
	@Override
	public Type getReturnType() {
		return returnType;
	}
	
	@Override
	public boolean requiresLocalContext() {
		return value.requiresLocalContext();
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof PutFieldOperation operation &&
				super.equals(operation) && value.equals(operation.value);
	}
}
