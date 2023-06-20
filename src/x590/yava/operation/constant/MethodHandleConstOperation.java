package x590.yava.operation.constant;

import x590.yava.constpool.MethodHandleConstant;
import x590.yava.type.TypeSize;

public final class MethodHandleConstOperation extends LdcOperation<MethodHandleConstant> {

	public MethodHandleConstOperation(MethodHandleConstant value) {
		super(TypeSize.WORD, value);
	}
}
