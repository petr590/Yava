package x590.yava.operation.operator;

import x590.yava.operation.ReturnableOperation;
import x590.yava.type.Type;

public abstract class OperatorOperation extends ReturnableOperation {
	
	public OperatorOperation(Type returnType) {
		super(returnType);
	}
	
	public abstract String getOperator();
	
	@Override
	public abstract int getPriority();
	
	protected boolean equals(OperatorOperation other) {
		return super.equals(other) && getOperator().equals(other.getOperator());
	}
}
