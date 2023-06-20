package x590.yava.instruction;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.context.PreDecompilationContext;
import x590.yava.operation.Operation;

/**
 * Описывает инструкцию байткода
 */
public interface Instruction {

	int NONE_INDEX = -1;

	/**
	 * Выполняется при декомпиляции кода
	 *
	 * @return Операцию, которая соответствует этой инструкции или {@code null},
	 * если операция не должна быть добавлена в код
	 */
	@Nullable Operation toOperation(DecompilationContext context);

	/**
	 * Выполняется до основной декомпиляции кода
	 */
	default void preDecompilation(PreDecompilationContext context) {
	}

	/**
	 * Выполняется после основной декомпиляции кода
	 */
	default void postDecompilation(DecompilationContext context) {
	}
}
