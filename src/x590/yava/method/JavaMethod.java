package x590.yava.method;

import it.unimi.dsi.fastutil.booleans.BooleanPredicate;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.util.IteratorUtil;
import x590.util.Logger;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.util.lazyloading.ObjectSupplierLazyLoading;
import x590.yava.JavaClassElement;
import x590.yava.attribute.AttributeType;
import x590.yava.attribute.Attributes;
import x590.yava.attribute.Attributes.Location;
import x590.yava.attribute.CodeAttribute;
import x590.yava.attribute.signature.MethodSignatureAttribute;
import x590.yava.clazz.ClassInfo;
import x590.yava.clazz.DecompilationStats;
import x590.yava.clazz.IClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.context.DecompilationContext;
import x590.yava.context.DisassemblerContext;
import x590.yava.context.StringifyContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.exception.decompilation.IllegalModifiersException;
import x590.yava.field.JavaField;
import x590.yava.io.*;
import x590.yava.main.Config;
import x590.yava.main.Yava;
import x590.yava.modifiers.MethodModifiers;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.operation.field.GetInstanceFieldOperation;
import x590.yava.operation.field.PutInstanceFieldOperation;
import x590.yava.operation.invoke.RecordSyntheticOperation;
import x590.yava.operation.load.LoadOperation;
import x590.yava.operation.returning.ReturnOperation;
import x590.yava.scope.MethodScope;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;
import x590.yava.util.IWhitespaceStringBuilder;
import x590.yava.util.WhitespaceStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static x590.yava.modifiers.Modifiers.*;

public final class JavaMethod extends JavaClassElement {

	private final MethodModifiers modifiers;
	private final MethodDescriptor descriptor;
	private MethodDescriptor genericDescriptor;

	private final Attributes attributes;
	private final CodeAttribute codeAttribute;
	private final @Nullable MethodSignatureAttribute signature;

	private final DisassemblerContext disassemblerContext;
	private @Nullable DecompilationContext decompilationContext;
	private final StringifyContext stringifyContext;

	private MethodScope methodScope = MethodScope.empty();

	private @Nullable MethodInfo methodInfo;

	private @Nullable String exceptionMessage;

	private boolean hasOverrideAnnotation;


	private final ObjectSupplierLazyLoading<VisibilityState> visibilityState;

	private enum VisibilityState {
		/**
		 * Метод не считается сгенерированным компилятором, выводится всегда
		 */
		NORMAL(showingAllowed -> true),

		/**
		 * Метод считается сгенерированным компилятором, выводится только с флагом {@link Config#showAutogenerated()}
		 */
		AUTOGENERATED(showingAllowed -> showingAllowed),

		/**
		 * Метод не должен выводиться даже с флагом {@link Config#showAutogenerated()}
		 */
		HIDDEN(showingAllowed -> false);

		private final BooleanPredicate canShow;

		VisibilityState(BooleanPredicate canShow) {
			this.canShow = canShow;
		}

		public boolean canShow(boolean showingAllowed) {
			return canShow.test(showingAllowed);
		}

		public boolean isAutogenerated() {
			return this != NORMAL;
		}

		public static VisibilityState valueOf(boolean value) {
			return value ? AUTOGENERATED : NORMAL;
		}
	}


	private VisibilityState initVisibilityState(ClassInfo classinfo) {

		if (modifiers.isSyntheticOrBridge()) {
			return VisibilityState.NORMAL;
		}

		if (signature != null && signature.hasGenericTypes() || attributes.has(AttributeType.EXCEPTIONS) ||
				attributes.has(AttributeType.RUNTIME_VISIBLE_ANNOTATIONS) || attributes.has(AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS)) {
			return VisibilityState.NORMAL;
		}

		var descriptor = this.descriptor;

		if (descriptor.isStaticInitializer()) { // static {}
			return VisibilityState.valueOf(methodScope.isEmpty());
		}

		var thisClassType = classinfo.getThisType();

		if (descriptor.isConstructor()) {

			// anonymous class constructor
			if (thisClassType.isAnonymous()) {
				return VisibilityState.AUTOGENERATED;
			}

			if (classinfo.getModifiers().isEnum()) {
				// enum constructor by default
				return VisibilityState.valueOf(descriptor.argumentsEquals(ClassType.STRING, PrimitiveType.INT) &&
						modifiers.isPrivate() && methodScope.isEmpty() && hasNoOtherConstructors(classinfo));
			}

			// record constructor by default
			if (classinfo.isRecord()) {

				List<Type> arguments = descriptor.getArguments();
				List<JavaField> recordComponents = classinfo.getRecordComponents();

				int recordComponentsCount = recordComponents.size();

				if (recordComponentsCount == descriptor.getArgumentsCount() &&
						IteratorUtil.iteratorsEquals(
								recordComponents.stream().map(field -> field.getDescriptor().getType()).iterator(),
								arguments.iterator(), Type::equals)) {

					List<Operation> operations = methodScope.getOperations();

					if (operations.size() == recordComponentsCount &&
							IntStream.range(0, recordComponentsCount).allMatch(
									index ->
											operations.get(index) instanceof PutInstanceFieldOperation putInstanceField &&
													putInstanceField.getObject().isThisObject() &&
													putInstanceField.getDescriptor().equals(recordComponents.get(index).getDescriptor()) &&
													putInstanceField.getValue() instanceof LoadOperation loadOperation &&
													loadOperation.getVariable() == methodScope.getDefinedVariable(index + 1)
							)
					) {
						return VisibilityState.AUTOGENERATED;
					}
				}
			}

			// constructor by default
			return VisibilityState.valueOf(descriptor.getArgumentsCount() - descriptor.getVisibleStartIndex(classinfo) == 0 &&
					modifiers.and(ACC_ACCESS_FLAGS) == classinfo.getModifiers().and(ACC_ACCESS_FLAGS) &&
					methodScope.isEmpty() && hasNoOtherConstructors(classinfo));
		}

		if (classinfo.getModifiers().isEnum()) {
			if (descriptor.equalsIgnoreClass(thisClassType, "valueOf", ClassType.STRING) || // Enum valueOf(String name)
					descriptor.equalsIgnoreClass(thisClassType.arrayType(), "values")) { // Enum[] values()
				return VisibilityState.AUTOGENERATED;
			}
		}

		if (classinfo.isRecord() &&
				methodScope.getOperationsCount() == 1 &&
				methodScope.getOperationAt(0) instanceof ReturnOperation returnOperation) {

			Operation operand = returnOperation.getOperand();

			if (operand instanceof RecordSyntheticOperation) {
				return VisibilityState.HIDDEN;
			}

			if (operand instanceof GetInstanceFieldOperation getField &&
					getField.getObject().isThisObject(modifiers) &&
					getField.getDescriptor().equals(descriptor.getReturnType(), descriptor.getDeclaringClass(), descriptor.getName())) {

				return VisibilityState.AUTOGENERATED;
			}
		}

		return VisibilityState.NORMAL;
	}

	private boolean hasNoOtherConstructors(ClassInfo classinfo) {
		return !classinfo.hasMethod(method -> method != this && method.descriptor.isConstructor());
	}


	private JavaMethod(ExtendedDataInputStream in, ClassInfo classinfo, ConstantPool pool) {
		this.modifiers = MethodModifiers.read(in);
		var descriptor = this.descriptor = MethodDescriptor.from(classinfo.getThisType(), in, pool);

		this.genericDescriptor = descriptor;

		this.attributes = Attributes.read(in, pool, Location.METHOD);
		this.codeAttribute = attributes.getOrDefaultEmpty(AttributeType.CODE);
		this.signature = attributes.getNullable(AttributeType.METHOD_SIGNATURE);

		Logger.logf("Disassembling of method %s", descriptor);

		this.disassemblerContext = DisassemblerContext.disassemble(pool, codeAttribute.getCode());
		this.stringifyContext = new StringifyContext(disassemblerContext, classinfo, this);
		this.visibilityState = new ObjectSupplierLazyLoading<>(() -> initVisibilityState(classinfo));
	}

	private JavaMethod(AssemblingInputStream in, ClassInfo classinfo, ConstantPool pool) {
		this.modifiers = MethodModifiers.parse(in);
		this.descriptor = MethodDescriptor.from(classinfo.getThisType(), in);

		this.genericDescriptor = descriptor;

		this.attributes = Attributes.parse(in, pool, Location.METHOD);
		this.codeAttribute = attributes.getOrDefaultEmpty(AttributeType.CODE);
		this.signature = attributes.getNullable(AttributeType.METHOD_SIGNATURE);

		this.disassemblerContext = DisassemblerContext.disassemble(pool, codeAttribute.getCode());
		this.stringifyContext = new StringifyContext(disassemblerContext, classinfo, this);
		this.visibilityState = new ObjectSupplierLazyLoading<>(() -> initVisibilityState(classinfo));
	}


	public static List<JavaMethod> readMethods(ExtendedDataInputStream input, ClassInfo classinfo, ConstantPool pool) {
		int length = input.readUnsignedShort();
		List<JavaMethod> methods = new ArrayList<>(length);

		for (int i = 0; i < length; i++) {
			methods.add(new JavaMethod(input, classinfo, pool));
		}

		return methods;
	}

	public static @Immutable List<JavaMethod> parseMethods(AssemblingInputStream in, ClassInfo classinfo, ConstantPool pool) {
		if (in.advanceIfHasNext("methods")) {
			in.requireNext('{');

			List<JavaMethod> methods = new ArrayList<>();

			while (!in.advanceIfHasNext('}')) {
				methods.add(new JavaMethod(in, classinfo, pool));
			}

			return Collections.unmodifiableList(methods);
		}

		return Collections.emptyList();
	}

	@Override
	public MethodModifiers getModifiers() {
		return modifiers;
	}

	public MethodDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * @return Generic дескриптор метода или {@link #descriptor}, если метод не содержит generic типов
	 */
	public MethodDescriptor getGenericDescriptor() {
		return genericDescriptor;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public CodeAttribute getCodeAttribute() {
		return codeAttribute;
	}


	public DisassemblerContext getDisassemblerContext() {
		return disassemblerContext;
	}

	public StringifyContext getStringifyContext() {
		return stringifyContext;
	}


	public MethodScope getMethodScope() {
		return methodScope;
	}

	public MethodInfo getMethodInfo() {
		return methodInfo == null ? methodInfo = new MethodInfo(descriptor, genericDescriptor, modifiers) : methodInfo;
	}

	public void beforeDecompilation(ClassInfo classinfo) {
		// Код проверки сигнатуры был перенесён в этот метод из конструктора,
		// так как при дизассемблировании модификаторы вложенного класса ещё не инициализированы,
		// что приводит к неправильной работе при декомпиляции статических вложенных классов
		if (signature != null) {
			signature.checkTypes(descriptor, descriptor.getVisibleStartIndex(classinfo), attributes.getNullable(AttributeType.EXCEPTIONS));
			genericDescriptor = signature.createGenericDescriptor(classinfo, descriptor);
		}
	}


	/**
	 * @return Встроенный код, если метод является синтетическим геттером или сеттером,
	 * сгенерированным компилятором для доступа к приватным полям вложенного или внешнего класса,
	 * иначе {@code null}
	 */
	public @Nullable Operation inlineIfAccessMethod(@Immutable List<Operation> arguments) {
		var modifiers = this.modifiers;
		var descriptor = this.descriptor;
		final int argumentsCount = descriptor.getArgumentsCount();

		if (argumentsCount != arguments.size()) {
			throw new IllegalArgumentException("Arguments count not matches with descriptor arguments count");
		}

		if (modifiers.allOf(ACC_STATIC | ACC_SYNTHETIC) && modifiers.isPackageVisible() &&
				descriptor.isPlain() &&
				descriptor.getReturnType() != PrimitiveType.VOID &&
				methodScope.getOperationsCount() == 1 &&
				methodScope.getOperationAt(0) instanceof ReturnOperation returnOperation
		) {

			Int2ObjectMap<Operation> varTable = new Int2ObjectArrayMap<>(argumentsCount);

			for (int i = 0; i < argumentsCount; i++) {
				varTable.put(i, arguments.get(i));
			}

			return returnOperation.getOperand().inline(varTable);
		}

		return null;
	}


	public void decompile(ClassInfo classinfo, DecompilationStats stats) {
		final var descriptor = this.descriptor;

		Logger.logf("Decompiling of method %s", descriptor);


		this.methodScope = MethodScope.of(classinfo, genericDescriptor, modifiers, codeAttribute,
				disassemblerContext.getInstructions().size(), codeAttribute.isEmpty() ? descriptor.countLocals(modifiers) : codeAttribute.getMaxLocalsCount());


		try {
			decompilationContext = DecompilationContext.decompile(disassemblerContext, classinfo, this, stats,
					disassemblerContext.getInstructions());

		} catch (DecompilationException ex) {
			ex.printStackTrace();
			exceptionMessage = ex.getFullMessage();
		}

		if (Yava.getConfig().useOverrideAnnotation() && descriptor.isPlain() && modifiers.isNotStatic()) {
			resolveOverrideAnnotation(classinfo);
		}
	}


	public @Nullable String getDecompilationException() {
		return exceptionMessage;
	}

	public boolean hasDecompilationException() {
		return exceptionMessage != null;
	}


	public void afterDecompilation() {
		if (decompilationContext != null)
			decompilationContext.afterDecompilation();
	}


	public void resolveOverrideAnnotation(ClassInfo classinfo) {
		if (!classinfo.getThisType().equals(ClassType.OBJECT)) {
			this.hasOverrideAnnotation = hasOverrideAnnotationInSuperClass(classinfo);
		}
	}


	private boolean hasOverrideAnnotationInClass(Optional<IClassInfo> optionalClassinfo) {
		if (optionalClassinfo.isEmpty()) {
			return false;
		}

		if (optionalClassinfo.get().hasMethodByDescriptor(descriptor::equalsIgnoreClassAndReturnType)) {
			return true;
		}

		return hasOverrideAnnotationInSuperClass(optionalClassinfo.get());
	}

	private boolean hasOverrideAnnotationInSuperClass(IClassInfo classinfo) {
		return hasOverrideAnnotationInClass(ClassInfo.findIClassInfo(classinfo.getOptionalSuperType())) ||
				classinfo.getInterfacesOrEmpty().stream()
						.anyMatch(superInterface -> hasOverrideAnnotationInClass(ClassInfo.findIClassInfo(superInterface)));
	}


	public boolean hasOverrideAnnotation() {
		return hasOverrideAnnotation;
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		attributes.addImports(classinfo);
		descriptor.addImports(classinfo);

		if (decompilationContext != null)
			decompilationContext.addImports(classinfo);

		if (hasOverrideAnnotation)
			classinfo.addImport(ClassType.OVERRIDE);
	}


	@Override
	public boolean canStringify(ClassInfo classinfo) {
		var modifiers = this.modifiers;
		var config = Yava.getConfig();

		return (!modifiers.isSyntheticOrBridge() ||
				modifiers.isSynthetic() && config.showSynthetic() ||
				modifiers.isBridge() && config.showBridge()
		) &&
				visibilityState.get().canShow(config.showAutogenerated()) &&
				!(descriptor.isStaticInitializer() && methodScope.isEmpty());
	}


	public int getLambdaPriority() {
		return methodScope.isLambdaNewArray() ? Priority.DEFAULT_PRIORITY : Priority.LAMBDA;
	}


	public void writeAsLambda(StringifyOutputStream out, int captured, List<Operation> capturedArguments) {

		if (codeAttribute.isEmpty()) {
			throw new DecompilationException("Cannot write method without Code attribute as lambda");
		}

		methodScope.assignVariablesNames();

		if (decompilationContext != null &&
				descriptor.argumentsEquals(PrimitiveType.INT) &&
				descriptor.getReturnType().isArrayType() &&
				methodScope.tryWriteAsLambdaNewArray(out, stringifyContext)) {

			return;
		}

		genericDescriptor.writeAsLambda(out, stringifyContext, attributes, captured);

		out.printsp().print("->");

		if (decompilationContext == null) {
			Type returnType = descriptor.getReturnType();

			out.printsp().print(
					returnType.isPrimitive() ?
							returnType == PrimitiveType.VOID ? "{}" :
									returnType == PrimitiveType.BOOLEAN ? "false" : "0" :
							"null");

			if (exceptionMessage != null) {
				out.print(" /* ").print(exceptionMessage).print(" */");
			}

		} else {
			for (int slot = 0; slot < capturedArguments.size(); slot++) {
				Operation argument = capturedArguments.get(slot);

				if (argument instanceof LoadOperation loadOperation)
					methodScope.getDefinedVariable(slot).setName(loadOperation.getVariable().getNullableName());
			}

			out.printUsingFunction(methodScope, stringifyContext, MethodScope::writeAsLabmda);
		}
	}


	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {

		methodScope.assignVariablesNames();

		if (hasOverrideAnnotation)
			out.printIndent().print('@').println(ClassType.OVERRIDE, classinfo);

		writeAnnotations(out, classinfo, attributes);

		out.printIndent().print(modifiersToString(classinfo), classinfo);

		final var signature = this.signature;

		if (signature != null) {
			signature.writeParametersIfNotEmpty(out, classinfo);
		}

		genericDescriptor.write(out, stringifyContext, attributes);

		attributes.getOrDefaultEmpty(AttributeType.EXCEPTIONS).write(out, classinfo, signature);

		out.printIfNotNull(attributes.getNullable(AttributeType.ANNOTATION_DEFAULT), classinfo);

		if (codeAttribute.isEmpty()) {
			out.write(';');

		} else if (decompilationContext == null) {
			out.print(';').print(" /* ").print(exceptionMessage != null ? exceptionMessage : "The reason for the exclusion is unknown")
					.print(" */");

		} else {
			out.print(methodScope, stringifyContext);
		}

		out.println();
	}

	@Override
	public String getModifiersTarget() {
		return "method " + descriptor.toString();
	}

	private IWhitespaceStringBuilder modifiersToString(ClassInfo classinfo) {

		if (descriptor.isStaticInitializer()) {
			if (modifiers.getValue() == ACC_STATIC) {
				return WhitespaceStringBuilder.empty();
			} else {
				throw new IllegalModifiersException("static initializer must have only static modifier");
			}
		}

		IWhitespaceStringBuilder str = new WhitespaceStringBuilder().printTrailingSpace();

		var modifiers = this.modifiers;
		var classModifiers = classinfo.getModifiers();

		switch (modifiers.and(ACC_ACCESS_FLAGS)) {
			case ACC_VISIBLE -> {
			}

			case ACC_PUBLIC -> { // Все нестатические методы интерфейса по умолчанию имеют модификатор public, поэтому в этом случае нам не нужно выводить public
				if (Yava.getConfig().printImplicitModifiers() || !classModifiers.isInterface())
					str.append("public");
			}

			case ACC_PRIVATE -> { // Конструкторы Enum по умолчанию имеют модификатор private, поэтому нам не нужно выводить private
				if (Yava.getConfig().printImplicitModifiers() || !(classModifiers.isEnum() && descriptor.isConstructor() && descriptor.getDeclaringClass().equals(classinfo.getThisType())))
					str.append("private");
			}

			case ACC_PROTECTED -> {
				str.append("protected");
			}

			default -> {
				throw new IllegalModifiersException(this, modifiers, ILLEGAL_ACCESS_MODIFIERS_MESSAGE);
			}
		}

		if (modifiers.isStatic())
			str.append("static");

		if (modifiers.isBridge()) {
			str.append("/* bridge */");
		} else if (modifiers.isSynthetic()) {
			str.append("/* synthetic */");
		} else if (visibilityState.get().isAutogenerated()) {
			str.append("/* autogenerated */");
		}

		if (modifiers.isAbstract()) {

			if (modifiers.anyOf(ACC_STATIC | ACC_FINAL | ACC_SYNCHRONIZED | ACC_NATIVE | ACC_STRICTFP))
				throw new IllegalModifiersException(this, modifiers,
						"abstract method cannot be static, final, synchronized, native or strict");

			if (classModifiers.isNotInterface())
				str.append("abstract");

		} else {
			if (classModifiers.isInterface() && modifiers.isNotStatic() && modifiers.isNotPrivate())
				str.append("default");
		}

		if (modifiers.isFinal()) str.append("final");
		if (modifiers.isSynchronized()) str.append("synchronized");

		if (modifiers.isNative() && modifiers.isStrictfp())
			throw new IllegalModifiersException(this, modifiers, "method cannot be both native and strictfp");

		if (modifiers.isNative()) str.append("native");
		else if (modifiers.isStrictfp()) str.append("strictfp");

		return str;
	}

	@Override
	public String toString() {
		return modifiers.toSimpleString() + " " + descriptor;
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
//		out.write(modifiersToString(classinfo), classinfo);
	}


	@Override
	public void serialize(ExtendedDataOutputStream out) {
		// TODO
	}
}
