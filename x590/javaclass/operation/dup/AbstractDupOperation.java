package x590.javaclass.operation.dup;

import x590.javaclass.context.DecompilationContext;
import x590.javaclass.context.StringifyContext;
import x590.javaclass.io.StringifyOutputStream;
import x590.javaclass.operation.Operation;
import x590.javaclass.type.Type;
import x590.javaclass.type.TypeSize;

public abstract class AbstractDupOperation extends Operation {
	
	protected final Operation value;
	
	public AbstractDupOperation(TypeSize size, DecompilationContext context) {
		this.value = context.stack.peekWithSize(size);
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.write(value, context);
	}
	
	@Override
	public Type getReturnType() {
		return value.getReturnType();
	}
	
	@Override
	public void allowImplicitCast() {
		value.allowImplicitCast();
	}
	
	@Override
	public void onCastReturnType(Type newType) {
		value.onCastReturnType(newType);
	}
	
	@Override
	public Operation original() {
		return value.original();
	}
	
	@Override
	public boolean requiresLocalContext() {
		return value.requiresLocalContext();
	}
	
	@Override
	public int getPriority() {
		return value.getPriority();
	}
}