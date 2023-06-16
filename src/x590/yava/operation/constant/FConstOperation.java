package x590.yava.operation.constant;

import x590.yava.constpool.FloatConstant;

public final class FConstOperation extends IntConvertibleConstOperation<FloatConstant> {
	
	public FConstOperation(FloatConstant constant) {
		super(constant);
	}
	
	public float getValue() {
		return constant.getValue();
	}
}
