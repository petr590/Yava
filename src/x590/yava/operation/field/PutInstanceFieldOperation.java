package x590.yava.operation.field;

import x590.yava.constpool.FieldrefConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;

public final class PutInstanceFieldOperation extends PutFieldOperation {
	
	private final Operation object;
	
	public PutInstanceFieldOperation(DecompilationContext context, int index) {
		super(context, index);
		this.object = popObject(context);
		init(context);
	}

	public PutInstanceFieldOperation(DecompilationContext context, FieldrefConstant fieldref) {
		super(context, fieldref);
		this.object = popObject(context);
		init(context);
	}
	
	public Operation getObject() {
		return object;
	}
	

	private void init(DecompilationContext context) {
		if(!canOmit && context.getDescriptor().isConstructor() &&
				context.currentScope() == context.getMethodScope() && !getValue().requiresLocalContext()) {
			
			if(context.getClassinfo().getField(getDescriptor()).setInstanceInitializer(getValue(), context))
				this.remove();
		}
		
		super.initIncData(context);
		super.initGenericDescriptor(object);
	}
	
	@Override
	public void writeName(StringifyOutputStream out, StringifyContext context) {
		if(!canOmitObject(context, object)) {
			out.printPrioritied(this, object, context, Associativity.LEFT).print('.');
		}
		
		super.writeName(out, context);
	}
	
	@Override
	public boolean requiresLocalContext() {
		return object.requiresLocalContext() || super.requiresLocalContext();
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof PutInstanceFieldOperation operation &&
				super.equals(operation) && object.equals(operation.object);
	}
}
