package x590.yava.operation.constant;

import x590.yava.constpool.LongConstant;

public final class LConstOperation extends IntConvertibleConstOperation<LongConstant> {
	
	public LConstOperation(LongConstant constant) {
		super(constant);
	}
	
	public long getValue() {
		return constant.getValue();
	}
}
