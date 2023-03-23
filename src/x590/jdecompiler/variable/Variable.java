package x590.jdecompiler.variable;

import x590.jdecompiler.attribute.LocalVariableTableAttribute.LocalVariableEntry;
import x590.jdecompiler.operation.Operation;
import x590.jdecompiler.scope.Scope;
import x590.jdecompiler.type.Type;
import x590.util.annotation.Nullable;

public interface Variable extends EmptyableVariable {
	
	@Override
	public default boolean isEmpty() {
		return false;
	}
	
	@Override
	public default Variable nonEmpty() {
		return this;
	}
	
	/** Устанавливает имя переменной */
	public void setName(String name);
	
	/** Добавляет переменной возможное имя */
	public void addPossibleName(String name);
	
	/** Делает переменную индексом */
	public default void makeAnIndex() {}
	
	
	/** Была ли переменная объявлена */
	public boolean isDefined();
	
	/** Объявить переменную */
	public void define();
	
	/** Объявить переменную и вернуть {@literal this} */
	public default Variable defined() {
		define();
		return this;
	}

	
	public Type getType();
	
	public void setProbableType(Type probableType);
	
	public void addAssignedOperation(Operation operation);
	
	public void castTypeToNarrowest(Type newType);
	
	public void castTypeToWidest(Type newType);
	
	
	public Scope getEnclosingScope();
	
	public void setEnclosingScope(Scope enclosingScope);
	
	
	@Override
	public default VariableWrapper wrapped() {
		return new WrappedVariable(this);
	}
	
	
	public static EmptyVariable empty() {
		return EmptyVariable.INSTANCE;
	}
	
	
	public static AbstractVariable valueOf(@Nullable LocalVariableEntry entry, Scope enclosingScope, Type type, boolean typeFixed) {
		return entry == null ?
				new UnnamedVariable(enclosingScope, type, typeFixed) :
				new NamedVariable(entry.name, enclosingScope, type, typeFixed);
	}
}
