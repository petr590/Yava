package x590.yava.operation.invoke;

import x590.yava.context.DecompilationContext;
import x590.yava.method.MethodDescriptor;
import x590.yava.operation.Operation;

public final class InvokeinterfaceOperation extends InvokeNonstaticOperation {
	
	public InvokeinterfaceOperation(DecompilationContext context, int index) {
		super(context, index);
	}
	
	public InvokeinterfaceOperation(DecompilationContext context, MethodDescriptor descriptor) {
		super(context, descriptor);
	}
	
	@Override
	protected String getInstructionName() {
		return "invokeinterface";
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof InvokeinterfaceOperation operation &&
				super.equals(operation);
	}
}
