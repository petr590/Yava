package x590.yava.operation.constant;

import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;

public final class EmptyStringConstOperation extends AbstractOperation {
	
	public static final EmptyStringConstOperation INSTANCE = new EmptyStringConstOperation();
	
	private EmptyStringConstOperation() {}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.write("\"\"");
	}
	
	@Override
	public Type getReturnType() {
		return ClassType.STRING;
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other;
	}
}
