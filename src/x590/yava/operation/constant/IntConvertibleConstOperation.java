package x590.yava.operation.constant;

import x590.yava.constpool.ConstableValueConstant;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

public abstract class IntConvertibleConstOperation<C extends ConstableValueConstant<?>> extends ConstOperation<C> {
	
	protected boolean implicit;
	
	public IntConvertibleConstOperation(C constant) {
		super(constant);
	}
	
	@Override
	public Type getImplicitType() {
		return constant.canImplicitCastToInt() ? PrimitiveType.INT : returnType;
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		constant.writeTo(out, context.getClassinfo(), returnType, implicit);
	}
	
	@Override
	protected void setImplicitCast(boolean implicit) {
		this.implicit = implicit;
	}
}
