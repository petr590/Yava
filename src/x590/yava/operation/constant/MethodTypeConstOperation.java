package x590.yava.operation.constant;

import x590.yava.constpool.MethodTypeConstant;
import x590.yava.type.TypeSize;

public final class MethodTypeConstOperation extends LdcOperation<MethodTypeConstant> {
	
	public MethodTypeConstOperation(MethodTypeConstant value) {
		super(TypeSize.WORD, value);
	}
}
