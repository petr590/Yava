package x590.yava.operation.constant;

import x590.yava.constpool.constvalue.ConstableValueConstant;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.util.StringUtil;

public abstract class IntConvertibleConstOperation<C extends ConstableValueConstant<?>> extends ConstOperation<C> {

	public IntConvertibleConstOperation(C constant) {
		super(constant);
	}

	@Override
	public Type getImplicitType() {
		return constant.canImplicitCastToInt() ? PrimitiveType.INT : returnType;
	}

	@Override
	protected void setImplicitCast(boolean implicit) {
		flags = implicit ?
				(flags | StringUtil.IMPLICIT) :
				(flags & ~StringUtil.IMPLICIT);
	}
}
