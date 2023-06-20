package x590.yava.operation.constant;

import x590.yava.constpool.DoubleConstant;

public final class DConstOperation extends IntConvertibleConstOperation<DoubleConstant> {

	public DConstOperation(DoubleConstant value) {
		super(value);
	}

	public double getValue() {
		return constant.getValue();
	}
}
