package x590.yava.instruction;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.context.PreDecompilationContext;
import x590.yava.operation.Operation;

/**
 * Описывает инструкцию байткода
 */
public interface Instruction {

	int NONE_INDEX = -1;

	int DEFAULT_CACHE_CAPACITY = 8;

	static <T extends Instruction> Int2ObjectMap<T> newCache() {
		return new Int2ObjectArrayMap<>(DEFAULT_CACHE_CAPACITY);
	}

	static <T extends Instruction> Int2ObjectMap<T> newCache(int capacity) {
		return new Int2ObjectArrayMap<>(capacity);
	}

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
	default void preDecompilation(PreDecompilationContext context) {}

	/**
	 * Выполняется после основной декомпиляции кода
	 */
	default void postDecompilation(DecompilationContext context) {}
}
