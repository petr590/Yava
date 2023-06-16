package x590.yava.operation.constant;

import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

public final class ZeroConstOperation extends AbstractOperation {
	
	public static final ZeroConstOperation INSTANCE = new ZeroConstOperation();
	
	private ZeroConstOperation() {}
	
	@Override
	public Type getReturnType() {
		return PrimitiveType.INT;
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.write('0');
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other;
	}
}
