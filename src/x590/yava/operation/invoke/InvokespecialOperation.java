package x590.yava.operation.invoke;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.clazz.JavaClass;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.io.StringifyOutputStream;
import x590.yava.main.Yava;
import x590.yava.method.MethodDescriptor;
import x590.yava.operation.Operation;
import x590.yava.operation.other.NewOperation;
import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.ReferenceType;

import java.util.LinkedList;

public final class InvokespecialOperation extends InvokeNonstaticOperation {

	/**
	 * Какой метод мы вызываем через super
	 */
	private enum SuperState {
		SUPERCLASS,     // метод суперкласса
		SUPERINTERFACE, // метод суперинтерфейса
		NONE            // не через super
	}

	private final SuperState superState;
	private final boolean isEnum, isRecord;
	private final Type returnType;
	private final @Nullable JavaClass nestedClass;

	private SuperState getSuperState(DecompilationContext context) {
		if (object.isThisObject(context.getMethodModifiers())) {

			ReferenceType clazz = getDescriptor().getDeclaringClass();

			if (clazz.equals(context.getClassinfo().getSuperType()))
				return SuperState.SUPERCLASS;

			if (context.getClassinfo().getInterfaces().stream().anyMatch(clazz::equals))
				return SuperState.SUPERINTERFACE;
		}

		return SuperState.NONE;
	}

	private Type getReturnType(DecompilationContext context) {
		if (getDescriptor().isConstructor() && object instanceof NewOperation newOperation) {

			if (context.pop() != newOperation)
				throw new DecompilationException("Cannot invoke constructor of object that is not new, invalid stack state");

			return newOperation.getType();
		}

		return getDescriptor().getReturnType();
	}

	private JavaClass getNestedClass(DecompilationContext context) {
		if (getDescriptor().isConstructor() &&
				getDescriptor().getDeclaringClass() instanceof ClassType classType && classType.isNested()) {

			JavaClass nestedClass = JavaClass.find(classType);

			if (nestedClass != null && nestedClass.isAnonymous()) {
				nestedClass.decompile(context.getStats());
			}

			return nestedClass;
		}

		return null;
	}

	public InvokespecialOperation(DecompilationContext context, int index) {
		super(context, index);
		this.superState = getSuperState(context);
		this.isEnum = context.getClassinfo().getModifiers().isEnum();
		this.isRecord = context.getClassinfo().isRecord();
		this.returnType = getReturnType(context);
		this.nestedClass = getNestedClass(context);
	}

	public InvokespecialOperation(DecompilationContext context, MethodDescriptor descriptor) {
		super(context, descriptor);
		this.superState = getSuperState(context);
		this.isEnum = context.getClassinfo().getModifiers().isEnum();
		this.isRecord = context.getClassinfo().isRecord();
		this.returnType = getReturnType(context);
		this.nestedClass = getNestedClass(context);
	}

	public InvokespecialOperation(DecompilationContext context, MethodDescriptor descriptor, Operation object) {
		super(context, descriptor, object);
		this.superState = getSuperState(context);
		this.isEnum = context.getClassinfo().getModifiers().isEnum();
		this.isRecord = context.getClassinfo().isRecord();
		this.returnType = getReturnType(context);
		this.nestedClass = getNestedClass(context);
	}

	@Override
	protected boolean canInvokeConstructor() {
		return true;
	}

	@Override
	protected String getInstructionName() {
		return "invokespecial";
	}

	@Override
	public @Nullable LinkedList<Operation> getStringBuilderChain(LinkedList<Operation> operands) {
		if (getDescriptor().isConstructor() &&
				getDescriptor().getDeclaringClass().equals(ClassType.STRING_BUILDER)) {

			if (getDescriptor().argumentsEmpty()) {
				return operands;
			}

			if (getDescriptor().argumentsEquals(ClassType.STRING)) {
				Operation argument = getArguments().get(0);

				if (argument instanceof InvokestaticOperation invokestatic &&
						invokestatic.getDescriptor().equals(ClassType.STRING, ClassType.STRING, "valueOf", 1)) {

					argument = invokestatic.getArguments().get(0);
				}

				operands.addFirst(argument);
				return operands;
			}
		}

		return null;
	}

	@Override
	public void afterDecompilation(DecompilationContext context) {
		if (nestedClass != null && nestedClass.isAnonymous()) {
			nestedClass.afterDecompilation();
		}
	}

	@Override
	public void addImports(ClassInfo classino) {
		if (nestedClass != null && nestedClass.isAnonymous()) {
			classino.addImport(returnType);
			nestedClass.addImports(classino);
		}
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if (getDescriptor().isConstructor()) {
			if (nestedClass != null && nestedClass.isAnonymous()) {
				nestedClass.writeAsNewAnonymousObject(out, context, this);

			} else {
				tryWriteObject(out, context);
				writeArguments(out, context);
			}

		} else {
			super.writeTo(out, context);
		}
	}

	@Override
	protected int skipArguments() {
		return Yava.getConfig().showSynthetic() ? 0 :
				isEnum && getDescriptor().isConstructor() ? MethodDescriptor.IMPLICIT_ENUM_ARGUMENTS :
						nestedClass != null && nestedClass.getModifiers().isNotStatic() ? MethodDescriptor.IMPLICIT_NONSTATIC_NESTED_CLASS_ARGUMENTS : 0;
	}

	@Override
	public void writeArguments(StringifyOutputStream out, StringifyContext context) {
		super.writeArguments(out, context);
	}

	@Override
	protected boolean tryWriteObject(StringifyOutputStream out, StringifyContext context) {

		return switch (superState) {

			case SUPERCLASS -> {
				out.write("super");
				yield true;
			}

			case SUPERINTERFACE -> {
				out.print(getDescriptor().getDeclaringClass(), context.getClassinfo()).print(".super");
				yield true;
			}

			case NONE -> super.tryWriteObject(out, context);
		};
	}

	@Override
	public Type getReturnType() {
		return returnType;
	}

	@Override
	public boolean canOmit() {
		return getDescriptor().isConstructor() && superState == SuperState.SUPERCLASS &&
				(isRecord ||
						!(isEnum ? Yava.getConfig().showSynthetic() : Yava.getConfig().showAutogenerated()) &&
								getDescriptor().getArgumentsCount() - skipArguments() == 0);
	}

	@Override
	protected boolean canOmitObject(StringifyContext context, Operation object) {
		return !getDescriptor().isConstructor() && super.canOmitObject(context, object);
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof InvokespecialOperation operation &&
				super.equals(operation) && returnType.equals(operation.returnType);
	}
}
