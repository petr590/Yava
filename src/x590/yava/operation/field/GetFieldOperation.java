package x590.yava.operation.field;

import x590.yava.constpool.FieldrefConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.field.FieldDescriptor;
import x590.yava.type.Type;

public abstract class GetFieldOperation extends FieldOperation {
	
	public GetFieldOperation(DecompilationContext context, int index) {
		super(context, index);
	}
	
	public GetFieldOperation(DecompilationContext context, FieldrefConstant fieldref) {
		super(context, fieldref);
	}

	public GetFieldOperation(DecompilationContext context, FieldDescriptor descriptor) {
		super(context, descriptor);
	}
	
	@Override
	public Type getReturnType() {
		return getGenericDescriptor().getType();
	}
	
	@Override
	public String getPossibleVariableName() {
		return getDescriptor().getName();
	}
}
