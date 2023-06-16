package x590.yava.operation;

import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

public interface BooleanOperation extends Operation {
	
	@Override
	public default Type getReturnType() {
		return PrimitiveType.BOOLEAN;
	}
}
