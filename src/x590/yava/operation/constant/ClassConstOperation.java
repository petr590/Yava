package x590.yava.operation.constant;

import x590.yava.constpool.ClassConstant;
import x590.yava.type.TypeSize;

public final class ClassConstOperation extends LdcOperation<ClassConstant> {
	
	public ClassConstOperation(ClassConstant value) {
		super(TypeSize.WORD, value);
	}
}
