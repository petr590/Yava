package x590.yava.operation.invoke;

import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.clazz.IClassInfo;
import x590.yava.constpool.MethodrefConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.io.StringifyOutputStream;
import x590.yava.method.MethodDescriptor;
import x590.yava.method.MethodInfo;
import x590.yava.operation.Operation;
import x590.yava.operation.OperationUtils;
import x590.yava.operation.OperationWithDescriptor;
import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.ReferenceType;
import x590.yava.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class InvokeOperation extends OperationWithDescriptor<MethodDescriptor> {

	private final List<Operation> arguments;

	private final @Immutable List<Operation> unmodifiableArguments;

	private List<Operation> popArguments(DecompilationContext context) {
		List<Type> argTypes = getDescriptor().getArguments();

		List<Operation> arguments = new ArrayList<>(argTypes.size());

		for (int i = argTypes.size(); --i >= 0; ) {
			arguments.add(context.popAsNarrowest(argTypes.get(i)));
		}

		// Аргументы идут в обратном порядке, поэтому надо перевернуть список
		Collections.reverse(arguments);

		return arguments;
	}

	protected static MethodDescriptor getDescriptor(DecompilationContext context, int index) {
		return context.pool.<MethodrefConstant>get(index).toDescriptor();
	}

	public @Immutable List<Operation> getArguments() {
		return unmodifiableArguments;
	}

	public int argumentsCount() {
		return arguments.size();
	}


	public InvokeOperation(DecompilationContext context, int index) {
		this(context, getDescriptor(context, index));
	}

	public InvokeOperation(DecompilationContext context, MethodDescriptor descriptor) {
		super(descriptor);

		if (descriptor.isStaticInitializer())
			throw new DecompilationException("Cannot invoke static initializer");

		if (descriptor.isConstructor() && !canInvokeConstructor())
			throw new DecompilationException("Cannot invoke constructor by the " + getInstructionName() + "instruction");

		this.arguments = popArguments(context);
		this.unmodifiableArguments = Collections.unmodifiableList(arguments);

		ClassInfo.findIClassInfo(descriptor.getDeclaringClass())
				.ifPresent(iclassinfo -> iclassinfo.findMethodInfo(descriptor)
						.ifPresent(methodInfo -> OperationUtils.tryInlineVarargs(context, descriptor, arguments, iclassinfo, methodInfo)));
	}


	@Override
	protected void initGenericDescriptor(@Nullable Operation object) {
		if (object == null) {
			super.initGenericDescriptor(null);
		} else {

			var descriptor = getDescriptor();
			var declaringClass = descriptor.getDeclaringClass();

			// Метод clone() для массивов при компиляции возвращает массив (это работает и с generic-массивами),
			// но на уровне байткода он возвращает Object, после этого делается приведение типа.
			// Поэтому returnType в generic-дескрипторе должен быть фактическим типом объекта

			if (declaringClass.isArrayType() && descriptor.equalsIgnoreClass(ClassType.OBJECT, "clone")) {
				setGenericDescriptor(MethodDescriptor.of(object.getReturnType(), declaringClass, "clone"));
			} else {
				super.initGenericDescriptor(object);
			}
		}


		var genericDescriptor = getGenericDescriptor();
		var arguments = this.arguments;

		for (int i = 0, size = arguments.size(); i < size; i++) {

			Type argType = genericDescriptor.getArgument(i);

			Operation argument = arguments.get(i).useAsNarrowest(argType);

			if (argType instanceof ReferenceType referenceType && !referenceType.equals(ClassType.OBJECT)) {
				argument = argument.castIfNull(referenceType);
			}

			arguments.set(i, argument);
		}
	}


	@Override
	protected Optional<? extends MethodInfo> findMemberInfo(IClassInfo classinfo, MethodDescriptor descriptor) {
		return classinfo.findMethodInfoInThisAndSuperClasses(descriptor);
	}


	protected boolean canInvokeConstructor() {
		return false;
	}

	protected abstract String getInstructionName();

	public @Nullable Operation getObject() {
		return null;
	}

	@Override
	public Type getReturnType() {
		return getGenericDescriptor().getReturnType();
	}


	private static final Pattern
			GETTER_PATTERN = Pattern.compile("get([A-Z].*)"),
			BOOLEAN_GETTER_PATTERN = Pattern.compile("is[A-Z].*"),
			CONVERTER_PATTERN = Pattern.compile(".*[a-z]To([A-Z].*)"),
			PATTERN_FOR_NOT_OMIT_THIS = Pattern.compile("equals(?:[A-Z].*)?");

	@Override
	public @Nullable String getPossibleVariableName() {
		String name = getDescriptor().getName();

		var matcher = GETTER_PATTERN.matcher(name);
		if (matcher.matches()) {
			return StringUtil.toLowerCamelCase(matcher.group(1));
		}

		matcher = BOOLEAN_GETTER_PATTERN.matcher(name);
		if (matcher.matches()) {
			return name;
		}

		matcher = CONVERTER_PATTERN.matcher(name);
		if (matcher.matches()) {
			return StringUtil.toLowerCamelCase(matcher.group(1));
		}

		if (name.equals("length") || name.equals("size") || name.equals("count")) {
			return Optional.of(getObject())
					.map(Operation::getPossibleVariableName)
					.map(objName -> objName + StringUtil.toTitleCase(name))
					.orElse(name);
		}

		return null;
	}


	@Override
	protected boolean canOmitObject(StringifyContext context, Operation object) {
		// Не опускать this для вызовов методов, название которых начинается с equals
		return super.canOmitObject(context, object) &&
				!PATTERN_FOR_NOT_OMIT_THIS.matcher(getDescriptor().getName()).matches();
	}


	protected void writeArguments(StringifyOutputStream out, StringifyContext context) {
		out.print('(').printAll(arguments, skipArguments(), context, ", ").print(')');
	}

	protected int skipArguments() {
		return 0;
	}

	@Override
	public boolean requiresLocalContext() {
		return arguments.stream().anyMatch(Operation::requiresLocalContext);
	}


	@Override
	public String toString() {
		return String.format("%s [ descriptor = %s, arguments = %s ]",
				this.getClass().getSimpleName(), getDescriptor(), arguments);
	}

	protected boolean equals(InvokeOperation other) {
		return super.equals(other) && arguments.equals(other.arguments);
	}
}
