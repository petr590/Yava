package x590.yava.type.reference.generic;

import x590.yava.clazz.ClassInfo;
import x590.yava.io.ExtendedOutputStream;

/**
 * Дженерик, не ограниченный ни сверху, ни снизу
 */
public final class AnyGenericType extends IndefiniteGenericType {

	public static final AnyGenericType INSTANCE = new AnyGenericType();

	private AnyGenericType() {
	}

	@Override
	public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
		out.write('?');
	}

	@Override
	public String getEncodedName() {
		return "*";
	}

	@Override
	public String getName() {
		return "?";
	}

	@Override
	public String toString() {
		return "?";
	}
}
