package x590.yava.type;

import x590.yava.type.special.AnyObjectType;
import x590.yava.type.special.AnyType;
import x590.yava.type.special.ExcludingBooleanType;

/**
 * Содержит объявления некоторых типов-синглтонов для более удобного доступа
 */
public final class Types {
	
	private Types() {}
	
	public static final Type
			ANY_TYPE = AnyType.INSTANCE,
			ANY_OBJECT_TYPE = AnyObjectType.INSTANCE,
			EXCLUDING_BOOLEAN_TYPE = ExcludingBooleanType.INSTANCE;
}
