package x590.yava.type.reference.generic;

import x590.util.annotation.Nullable;
import x590.yava.io.ExtendedStringInputStream;
import x590.yava.type.reference.ReferenceType;

/**
 * Wildcard-дженерик, ограниченный сверху, такой как {@code ? extends T}
 */
public final class ExtendingGenericType extends BoundedGenericType {

	public ExtendingGenericType(ReferenceType type) {
		super(type);
	}

	public ExtendingGenericType(ExtendedStringInputStream in) {
		super(in);
	}

	@Override
	public @Nullable ReferenceType getSuperType() {
		return getBound();
	}

	@Override
	protected String encodedBound() {
		return "+";
	}

	@Override
	protected String bound() {
		return "extends";
	}

	@Override
	public ReferenceType replaceWildcardIndicatorsToBound(int index, GenericParameters<GenericDeclarationType> parameters) {
		return getBound();
	}
}
