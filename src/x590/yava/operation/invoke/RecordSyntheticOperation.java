package x590.yava.operation.invoke;

import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.ReturnableOperation;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;

public final class RecordSyntheticOperation extends ReturnableOperation {
	
	public static final RecordSyntheticOperation
			TO_STRING = new RecordSyntheticOperation(ClassType.STRING),
			HASH_CODE = new RecordSyntheticOperation(PrimitiveType.INT),
			EQUALS    = new RecordSyntheticOperation(PrimitiveType.BOOLEAN);
	
	private RecordSyntheticOperation(Type returnType) {
		super(returnType);
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		throw new UnsupportedOperationException("Unexpected RecordSyntheticOperation in the code");
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other;
	}
}
