package x590.yava.operation.constant;

import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.operation.cast.CastOperation;
import x590.yava.type.Type;
import x590.yava.type.Types;
import x590.yava.type.reference.ReferenceType;

public final class AConstNullOperation extends AbstractOperation {

	public static final AConstNullOperation INSTANCE = new AConstNullOperation();

	private AConstNullOperation() {}


	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.write("null");
	}

	@Override
	public Type getReturnType() {
		return Types.ANY_OBJECT_TYPE;
	}

	@Override
	public Operation castIfNecessary(ReferenceType clazz) {
		return castIfNull(clazz);
	}

	@Override
	public Operation castIfNull(ReferenceType clazz) {
		return CastOperation.of(Types.ANY_OBJECT_TYPE, clazz, false, this);
	}

	@Override
	public boolean equals(Operation other) {
		return this == other;
	}
}
