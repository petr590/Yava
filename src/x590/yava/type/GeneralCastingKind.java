package x590.yava.type;

/**
 * Вид преобразования типов к общему типу. Указывает, в каком контексте происходит преобразование
 * к общему типу. Используется в методе {@link Type#castToGeneral(Type, GeneralCastingKind)}.
 */
public enum GeneralCastingKind implements ICastingKind {
	/**
	 * Оператор сравнения '>', '>=', '<', '<='
	 */
	COMPARISON,

	/**
	 * Операторы сравнения `==` и `!=`
	 */
	EQUALS_COMPARISON,

	/**
	 * Любой бинарный оператор
	 */
	BINARY_OPERATOR,

	/**
	 * Тернарный оператор
	 */
	TERNARY_OPERATOR;

	private final String lowerCaseName;

	GeneralCastingKind() {
		this.lowerCaseName = name().toLowerCase().replace('_', ' ');
	}

	@Override
	public String lowerCaseName() {
		return lowerCaseName;
	}
}
