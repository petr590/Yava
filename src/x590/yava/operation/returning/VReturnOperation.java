package x590.yava.operation.returning;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.operation.VoidOperation;
import x590.yava.type.primitive.PrimitiveType;

public final class VReturnOperation extends AbstractOperation implements VoidOperation {

	private static final VReturnOperation INSTANCE = new VReturnOperation();

	private VReturnOperation() {
	}

	/**
	 * @return Единственный экземпляр класса
	 */
	public static VReturnOperation getInstance() {
		return INSTANCE;
	}

	/**
	 * Проверяет, что метод переданного {@code context} возвращает {@code void}.
	 *
	 * @return Единственный экземпляр класса
	 * @throws {@link DecompilationException}, если метод возвращает <b>не {@code void}</b>.
	 */
	public static VReturnOperation getInstance(DecompilationContext context) {
		if (context.getGenericDescriptor().getReturnType() != PrimitiveType.VOID)
			throw new DecompilationException("The method return type (" + context.getGenericDescriptor().getReturnType() + ")" +
					" does not match type of the `return` instruction");

		return INSTANCE;
	}

	@Override
	public boolean isTerminable() {
		return true;
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		out.write("return");
	}

	@Override
	public boolean equals(Operation other) {
		return this == other;
	}
}
