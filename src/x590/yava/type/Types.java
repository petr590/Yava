package x590.yava.type;

import x590.yava.type.reference.generic.AnyGenericType;
import x590.yava.type.special.AnyObjectType;
import x590.yava.type.special.AnyType;
import x590.yava.type.special.ExcludingBooleanType;

/**
 * Содержит объявления некоторых типов-синглтонов для более удобного доступа
 */
public final class Types {

	private Types() {}

	public static final AnyType ANY_TYPE = AnyType.INSTANCE;
	public static final AnyObjectType ANY_OBJECT_TYPE = AnyObjectType.INSTANCE;
	public static final AnyGenericType ANY_GENERIC_TYPE = AnyGenericType.INSTANCE;
	public static final ExcludingBooleanType EXCLUDING_BOOLEAN_TYPE = ExcludingBooleanType.INSTANCE;
}
