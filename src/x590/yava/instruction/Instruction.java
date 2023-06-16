package x590.yava.instruction;

import x590.yava.context.DecompilationContext;
import x590.yava.context.PreDecompilationContext;
import x590.yava.operation.Operation;
import x590.util.annotation.Nullable;

/**
 * Описывает инструкцию байткода
 */
public interface Instruction {
	
	public static final int NONE_INDEX = -1;
	
	/** Выполняется при декомпиляции кода
	 * @return Операцию, которая соответствует этой инструкции или {@code null},
	 * если операция не должна быть добавлена в код */
	public @Nullable Operation toOperation(DecompilationContext context);
	
	/** Выполняется до основной декомпиляции кода */
	public default void preDecompilation(PreDecompilationContext context) {}
	
	/** Выполняется после основной декомпиляции кода */
	public default void postDecompilation(DecompilationContext context) {}
}
