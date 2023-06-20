package x590.yava.type.reference;

import x590.yava.exception.decompilation.IllegalTypeException;
import x590.yava.type.Type;

/**
 * Описывает тип, который является массивом.
 * Должен быть подклассом {@link Type}
 */
public interface IArrayType {

	/**
	 * Приводит {@code this} к типу {@link Type}
	 */
	default Type asType() {
		return (Type) this;
	}

	/**
	 * @throws IllegalTypeException если тип члена массива получить не удалось
	 */
	Type getMemberType();

	/**
	 * @throws IllegalTypeException если тип элемента массива получить не удалось
	 */
	Type getElementType();

	/**
	 * @return Уровень вложенности массива
	 */
	int getNestingLevel();

	/**
	 * @param nestingLevel уровень вложенности элемента
	 * @return Вложенный тип элемента
	 * @throws IllegalArgumentException если {@code nestLevel} меньше 1
	 *                                  или больше {@link #getNestingLevel()}
	 */
	default Type getNestedElementType(int nestingLevel) {
		if (nestingLevel <= 0) {
			throw new IllegalArgumentException("nestingLevel " + nestingLevel + " less than 1");
		}

		Type elementType = getNestedElementTypeRecursive(nestingLevel);

		if (elementType == null) {
			throw new IllegalArgumentException("nestingLevel " + nestingLevel + " is too deep for array type " + this);
		}

		return elementType;
	}

	private Type getNestedElementTypeRecursive(int nestLevel) {
		if (nestLevel == 1) {
			return getElementType();
		}

		if (getElementType() instanceof IArrayType arrayElementType) {
			return arrayElementType.getNestedElementTypeRecursive(nestLevel - 1);
		}

		return null;
	}
}
