package x590.yava.instruction.scope;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.context.DisassemblerContext;
import x590.yava.operation.Operation;
import x590.yava.operation.condition.BooleanConstOperation;
import x590.yava.operation.condition.CompareOperation;
import x590.yava.operation.condition.CompareType;
import x590.yava.operation.condition.ConditionOperation;
import x590.yava.scope.IfScope;
import x590.yava.scope.LoopScope;
import x590.yava.scope.Scope;

import java.util.function.Supplier;

public abstract class IfInstruction extends TransitionInstruction {

	private final Role role;

	public enum Role {
		IF, LOOP
	}


	private @Nullable LoopScope loopScope;


	public IfInstruction(DisassemblerContext context, int offset) {
		super(context, offset);
		this.role = targetPos >= context.currentPos() ? Role.IF : Role.LOOP;
	}

	public Role getRole() {
		return role;
	}


	@Override
	public @Nullable Operation toOperationBeforeTargetIndex(DecompilationContext context) {
		if (role == Role.LOOP) {
			return loopScope = new LoopScope(context, targetIndex - 1, fromIndex, BooleanConstOperation.FALSE);
		}

		return null;
	}

	@Override
	public @Nullable Scope toScope(DecompilationContext context) {

		int targetIndex = this.targetIndex;

		context.saveStackState(targetIndex);

		ConditionOperation condition = getCondition(context);

		switch (role) {
			case IF -> {
				if (recognizeIfScope(context, context.currentScope(), context.currentExpressionStartIndex(), targetIndex, condition::invert)) {
					return null;
				}

				return new IfScope(context, targetIndex, condition);
			}

			case LOOP -> {
				var loopScope = this.loopScope;

				if (loopScope != null) {
					loopScope.setCondition(loopScope.getCondition().or(condition));
					loopScope.setConditionStartIndex(context.currentExpressionStartIndex());
				}
			}
		}

		return null;
	}


	public static boolean recognizeIfScope(DecompilationContext context, Scope currentScope, int conditionStartIndex, int endIndex, Supplier<ConditionOperation> conditionGetter) {
		if (currentScope instanceof IfScope ifScope) {

			if (ifScope.endIndex() == endIndex && ifScope.startIndex() == conditionStartIndex) {
				ifScope.setConditionAndUpdate(ifScope.getCondition().and(conditionGetter.get()), context);
				return true;

			} else if (ifScope.endIndex() == context.currentIndex() + 1) {
				ifScope.setEndIndex(endIndex);
				ifScope.setConditionAndUpdate(ifScope.getCondition().invert().or(conditionGetter.get()), context);
				return true;
			}
		}

		return false;
	}


	public ConditionOperation getCondition(DecompilationContext context) {
		return CompareOperation.valueOf(context, getCompareType());
	}

	public abstract CompareType getCompareType();
}
