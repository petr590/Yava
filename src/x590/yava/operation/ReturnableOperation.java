package x590.yava.operation;

import x590.yava.type.CastingKind;
import x590.yava.type.Type;

import java.util.Objects;

/**
 * Операция с определённым возвращаемым значением, которое может меняться
 */
public abstract class ReturnableOperation extends AbstractOperation {

	protected Type returnType;

	public ReturnableOperation(Type returnType) {
		this.returnType = Objects.requireNonNull(returnType);
	}

	@Override
	public Type getReturnType() {
		return returnType;
	}

	@Override
	protected void onCastReturnType(Type newType, CastingKind casting) {
		this.returnType = Objects.requireNonNull(newType);
	}


	@Override
	public final boolean deduceType() {
		var returnType = this.returnType;
		Type deducedType = getDeducedType(returnType);

		if (!returnType.equals(deducedType)) {
			this.returnType = deducedType;
			return true;
		}

		return false;
	}

	protected Type getDeducedType(Type returnType) {
		return returnType;
	}

	@Override
	public void reduceType() {
		this.returnType = returnType.reduced();
	}

	protected boolean equals(ReturnableOperation other) {
		return returnType.equals(other.returnType);
	}
}
