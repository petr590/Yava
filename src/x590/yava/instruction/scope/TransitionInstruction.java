package x590.yava.instruction.scope;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.context.DisassemblerContext;
import x590.yava.context.PreDecompilationContext;
import x590.yava.operation.Operation;

/**
 * Хранит endPos для scope-а, потом он преобразуется в индекс
 */
public abstract class TransitionInstruction extends ScopeInstruction {

	protected final int fromPos, targetPos;

	protected int
			fromIndex = NONE_INDEX,
			targetIndex = NONE_INDEX;

	public TransitionInstruction(DisassemblerContext context, int offset) {
		this.fromPos = context.currentPos();
		this.targetPos = fromPos + offset;
	}

	public int getTargetPos() {
		return targetPos;
	}


	@Override
	public void preDecompilation(PreDecompilationContext context) {

		assert context.currentPos() == fromPos;

		this.fromIndex = context.posToIndex(fromPos);
		this.targetIndex = context.posToIndex(targetPos);
	}

	/**
	 * Запускается при декомпиляции на индексе на один меньше, чем индекс, соответствующий {@link #targetPos}
	 */
	public abstract @Nullable Operation toOperationBeforeTargetIndex(DecompilationContext context);
}
