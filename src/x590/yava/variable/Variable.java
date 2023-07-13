package x590.yava.variable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.util.annotation.Nullable;
import x590.yava.attribute.code.LocalVariableTableAttribute.LocalVariableEntry;
import x590.yava.operation.Operation;
import x590.yava.scope.Scope;
import x590.yava.type.CastingKind;
import x590.yava.type.Type;

import java.util.Optional;

/**
 * Переменная, которая точно не является пустой
 */
public interface Variable extends EmptyableVariable {

	@Override
	default boolean isEmpty() {
		return false;
	}

	@Override
	default Variable nonEmpty() {
		return this;
	}

	/**
	 * Проверяет, определено ли имя переменной
	 */
	boolean hasName();

	/**
	 * @return Имя переменной
	 * @throws IllegalStateException Если имя ещё не определено
	 */
	default String getName() {
		String name = getNullableName();
		if (name != null)
			return name;

		throw new IllegalStateException("Name for variable " + this + " yet not assigned");
	}

	/**
	 * @return Имя переменной или {@code null}, если имя ещё не определено
	 */
	@Nullable String getNullableName();

	/**
	 * @return Вероятное имя переменной или {@code null}, если нет ни одного
	 */
	@Nullable String getPossibleName();

	/**
	 * Устанавливает имя переменной
	 */
	void setName(String name);

	/**
	 * Добавляет переменной возможное имя, если оно не {@code null}
	 */
	void addPossibleName(@Nullable String name);

	/**
	 * Делает переменную индексом
	 */
	default void makeAnIndex() {}


	/**
	 * Была ли переменная объявлена
	 */
	boolean isDefined();

	/**
	 * Объявляет переменную
	 */
	void define();

	/**
	 * Объявляет переменную и возвращает её
	 */
	default Variable defined() {
		define();
		return this;
	}


	Type getType();

	void setProbableType(Type probableType);

	void addAssignedOperation(Operation operation);

	void castTypeToNarrowest(Type newType);

	void castTypeToWidest(Type newType);

	void castTypeTo(Type newType, CastingKind kind);


	Scope getEnclosingScope();

	void setEnclosingScope(Scope enclosingScope);


	@Nullable Int2ObjectMap<String> getEnumTable();

	void setEnumTable(@Nullable Int2ObjectMap<String> enumTable);


	@Override
	default VariableWrapper wrapped() {
		return new WrappedVariable(this);
	}

	@Override
	default Variable unwrapped() {
		return this;
	}


	static EmptyVariable empty() {
		return EmptyVariable.INSTANCE;
	}


	static AbstractVariable valueOf(Optional<LocalVariableEntry> optionalEntry, Scope enclosingScope, Type type, boolean typeFixed) {
		return optionalEntry.isEmpty() ?
				new UnnamedVariable(enclosingScope, type, typeFixed) :
				new NamedVariable(optionalEntry.get().getName(), enclosingScope, type, typeFixed);
	}
}
