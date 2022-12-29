package x590.javaclass.operation.constant;

import x590.javaclass.type.Type;

public abstract class IntConvertibleConstOperation extends ConstOperation {
	
	protected boolean implicit;
	
	public IntConvertibleConstOperation(Type returnType) {
		super(returnType);
	}
	
	@Override
	public abstract Type getImplicitType();
	
	@Override
	public void allowImplicitCast() {
		implicit = true;
	}
}