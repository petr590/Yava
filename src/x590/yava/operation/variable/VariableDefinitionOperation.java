package x590.yava.operation.variable;

import x590.yava.operation.Operation;
import x590.yava.variable.Variable;

public interface VariableDefinitionOperation extends Operation {
	
	/**
	 * Скрывает объявление типа переменной. Нужно, например,
	 * для корректной инициализации в начале цикла for
	 */
	public void hideTypeDefinition();

	public Variable getVariable();
}
