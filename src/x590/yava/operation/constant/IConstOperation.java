package x590.yava.operation.constant;

import x590.yava.constpool.constvalue.IntegerConstant;
import x590.yava.operation.Operation;
import x590.yava.operation.cast.CastOperation;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

public final class IConstOperation extends ConstOperation<IntegerConstant> {

	public IConstOperation(IntegerConstant constant) {
		super(constant);
	}

	public int getValue() {
		return constant.getValue();
	}

	@Override
	public Operation castIfShortOrByteLiteral(Type type) {
		return CastOperation.of(PrimitiveType.INT, type, false, this);
	}
}
