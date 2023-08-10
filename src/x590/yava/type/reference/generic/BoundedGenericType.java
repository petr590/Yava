package x590.yava.type.reference.generic;

import x590.yava.clazz.ClassInfo;
import x590.yava.io.ExtendedOutputStream;
import x590.yava.io.ExtendedStringInputStream;
import x590.yava.type.reference.ReferenceType;

/**
 * Wildcard-дженерик, ограниченный сверху или снизу
 */
public abstract sealed class BoundedGenericType extends IndefiniteGenericType
		permits ExtendingGenericType, SuperGenericType {

	private final ReferenceType bound;
	private final String encodedName, name;

	public BoundedGenericType(ReferenceType bound) {
		this.bound = bound;
		this.name = encodedBound() + bound.getName();
		this.encodedName = "? " + bound() + bound.getEncodedName();
	}

	public BoundedGenericType(ExtendedStringInputStream in) {
		this(parseSignatureParameter(in));
	}

	public ReferenceType getBound() {
		return bound;
	}

	/**
	 * @return {@code "+"} или {@code "-"}, в зависимости от типа
	 */
	protected abstract String encodedBound();

	/**
	 * @return {@code "extends"} или {@code "super"}, в зависимости от типа
	 */
	protected abstract String bound();

	@Override
	public void addImports(ClassInfo classinfo) {
		bound.addImports(classinfo);
	}

	@Override
	public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
		out.printsp('?').printsp(bound()).printObject(bound, classinfo);
	}

	@Override
	public String getEncodedName() {
		return encodedName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "? " + bound() + ' ' + getBound().toString();
	}
}
