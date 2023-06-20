package x590.yava.scope;

import x590.util.annotation.Nullable;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.field.FieldDescriptor;
import x590.yava.instruction.scope.IfInstruction;
import x590.yava.io.StringifyOutputStream;
import x590.yava.main.Yava;
import x590.yava.operation.Operation;
import x590.yava.operation.condition.AndOperation;
import x590.yava.operation.condition.BooleanConstOperation;
import x590.yava.operation.condition.CompareWithZeroOperation;
import x590.yava.operation.condition.ConditionOperation;
import x590.yava.operation.field.GetStaticFieldOperation;
import x590.yava.operation.invoke.InvokespecialOperation;
import x590.yava.operation.other.AThrowOperation;
import x590.yava.operation.other.NewOperation;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;

import java.util.regex.Pattern;

public class IfScope extends ConditionalScope {

	private @Nullable ElseScope elseScope, prevElseScope;

	public IfScope(DecompilationContext context, int endIndex, ConditionOperation condition) {
		super(context, endIndex, condition.invert());
	}


	public void setConditionAndUpdate(ConditionOperation condition, DecompilationContext context) {
		setCondition(condition);
		update(context);
	}

	/**
	 * Обновляет scope: если он является вторым условием оператора or или and,
	 * то условие superScope изменяется, и этот scope удаляется
	 */
	public void update(DecompilationContext context) {
		if (IfInstruction.recognizeIfScope(context, superScope(), conditionStartIndex(), endIndex(), this::getCondition)) {
			this.remove();
		}
	}


	public void addElse(DecompilationContext context, int endIndex) {
		if (elseScope != null)
			throw new IllegalArgumentException("Cannot set another elseScope");

		elseScope = new ElseScope(context, endIndex, this);
		superScope().addOperation(elseScope, context);
		context.addScopeToQueue(elseScope);
	}


	void setPrevElse(ElseScope prevElseScope) {
		this.prevElseScope = prevElseScope;
	}


	@Override
	public boolean isTerminable() {
		return false;
	}

	boolean canSelfOmitCurlyBrackets() {
		return super.canOmitCurlyBrackets() && (
					/*
					 Для кода такого вида мы не можем опустить фигурные скобки,
					 иначе будет синтаксически другое выражение:
					
						if(condition1) {
							if(condition2)
								code1;
							
						} else {
							code2;
						}
					
					 Для избегания этой ситуации я написал код ниже:
					*/
				(elseScope == null || getOperationsCount() != 1 || !(getOperationAt(0) instanceof IfScope))
		);
	}

	boolean canSelfOmitCurlyBracketsForward() {
		return canSelfOmitCurlyBrackets() &&
				(elseScope == null || elseScope.canSelfOmitCurlyBracketsForward());
	}

	boolean canSelfOmitCurlyBracketsBackward() {
		return canSelfOmitCurlyBrackets() &&
				(prevElseScope == null || prevElseScope.canSelfOmitCurlyBracketsBackward());
	}

	@Override
	protected boolean canOmitCurlyBrackets() {
		return canSelfOmitCurlyBracketsForward() &&
				(prevElseScope == null || prevElseScope.canSelfOmitCurlyBracketsBackward());
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if (!tryWriteAsAssertion(out, context))
			super.writeTo(out, context);
	}


	@Override
	protected void writeHeader(StringifyOutputStream out, StringifyContext context) {
		out.print("if(").print(getCondition(), context).print(')');
	}

	private static final Pattern ASSERTIONS_DISABLED_FIELD_PATTERN = Pattern.compile("\\$assertionsDisabled(_\\d+)?");

	private static final ClassType ASSERTION_ERROR = ClassType.fromClass(AssertionError.class);


	private boolean tryWriteAsAssertion(StringifyOutputStream out, StringifyContext context) {

		var condition = getCondition();

		if (condition instanceof AndOperation and) {
			if (and.operand1() instanceof CompareWithZeroOperation compare) {
				return tryWriteAsAssertion(out, context, compare, and.operand2().invert());
			}

		} else if (condition instanceof CompareWithZeroOperation compare) {
			return tryWriteAsAssertion(out, context, compare, BooleanConstOperation.FALSE);
		}

		return false;
	}


	private boolean tryWriteAsAssertion(StringifyOutputStream out, StringifyContext context,
										CompareWithZeroOperation assertionsDisabledOperation, ConditionOperation condition) {

		if (assertionsDisabledOperation.operand() instanceof GetStaticFieldOperation getstatic) {

			FieldDescriptor fieldDescriptor = getstatic.getDescriptor();

			if (ASSERTIONS_DISABLED_FIELD_PATTERN.matcher(fieldDescriptor.getName()).matches() &&
					fieldDescriptor.getType() == PrimitiveType.BOOLEAN &&
					fieldDescriptor.getDeclaringClass().equals(context.getClassinfo().getThisType())) {

				var assertionsDisabledField = context.getClassinfo().findField(fieldDescriptor);

				if (assertionsDisabledField.isPresent() &&
						assertionsDisabledField.get().getModifiers().isSynthetic()) {

					var operations = getOperations();

					if (operations.size() == 1 &&
							operations.get(0) instanceof AThrowOperation athrow &&
							athrow.getOperand() instanceof InvokespecialOperation invokespecial &&
							invokespecial.getObject() instanceof NewOperation) {

						var methodDescriptor = invokespecial.getDescriptor();

						if (methodDescriptor.isConstructorOf(ASSERTION_ERROR) && methodDescriptor.getArgumentsCount() <= 1) {
							boolean printBrackets = Yava.getConfig().printBracketsAroundAssertStatements();

							out.print("assert").print(printBrackets ? '(' : ' ').print(condition, context);

							if (printBrackets)
								out.write(')');

							if (methodDescriptor.getArgumentsCount() > 0) {
								out.print(" : ").print(invokespecial.getArguments().get(0), context);
							}

							out.write(';');

							return true;
						}
					}
				}
			}
		}

		return false;
	}


	@Override
	public void writeSeparator(StringifyOutputStream out, StringifyContext context, Operation nextOperation) {
		if (elseScope == null)
			super.writeSeparator(out, context, nextOperation);
	}
}
