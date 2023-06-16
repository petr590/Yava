package x590.yava.operation.constant;

import x590.yava.constpool.ConstValueConstant;
import x590.yava.exception.decompilation.TypeSizeMismatchException;
import x590.yava.type.TypeSize;

public abstract class LdcOperation<CT extends ConstValueConstant> extends ConstOperation<CT> {
	
	public LdcOperation(TypeSize size, CT constant) {
		super(constant);
		
		if(constant.getType().getSize() != size)
			throw new TypeSizeMismatchException(size, constant.getType().getSize(), constant.getType());
	}
}
