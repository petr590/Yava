package x590.yava.variable;

/**
 * Обёртки над переменными быди написаны дабы не ломать уже существующий код,
 * использующий переменные. Необходимо было изменять переменную, не меняя ссылку на неё.
 * Есть 2 типа обёрток: пустая (нет смысла создавать экземпляры пустой обёртки каждый раз,
 * лучше использовать синглтон) и непустая (хранит реальную переменную).
 * Используются точно также, как и обычные переменные.
 */
public interface EmptyableVariableWrapper extends EmptyableVariable {


	@Override
	VariableWrapper nonEmpty();

	/**
	 * Присваивает новую переменную, возвращает обёртку.
	 * Для пустой обёртки возвращает новую, для непустой - {@code this}
	 */
	VariableWrapper assign(Variable other);


	@Override
	default EmptyableVariableWrapper wrapped() {
		return this;
	}
}
