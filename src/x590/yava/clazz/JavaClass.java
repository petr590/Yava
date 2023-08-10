package x590.yava.clazz;

import x590.util.LoopUtil;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.JavaClassElement;
import x590.yava.attribute.*;
import x590.yava.attribute.Attributes.Location;
import x590.yava.attribute.InnerClassesAttribute.InnerClassEntry;
import x590.yava.attribute.signature.ClassSignatureAttribute;
import x590.yava.clazz.DecompilationStage.DecompilationStageHolder;
import x590.yava.constpool.ConstantPool;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.exception.decompilation.IllegalClassHeaderException;
import x590.yava.exception.decompilation.IllegalModifiersException;
import x590.yava.exception.disassembling.ClassFormatException;
import x590.yava.field.JavaEnumField;
import x590.yava.field.JavaField;
import x590.yava.io.*;
import x590.yava.main.Yava;
import x590.yava.main.performing.AbstractPerforming.PerformingType;
import x590.yava.method.JavaMethod;
import x590.yava.modifiers.ClassModifiers;
import x590.yava.operation.invoke.InvokespecialOperation;
import x590.yava.serializable.JavaSerializable;
import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.RealReferenceType;
import x590.yava.util.IWhitespaceStringBuilder;
import x590.yava.util.WhitespaceStringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.io.File.separatorChar;
import static x590.yava.modifiers.Modifiers.*;

/**
 * Описывает декомпилируемый класс
 */
public final class JavaClass extends JavaClassElement implements JavaSerializable {


	public static final int SIGNATURE = 0xCAFEBABE;

	private static final Map<ClassType, JavaClass> CLASSES = new HashMap<>();


	private final Version version;
	private final ConstantPool pool;
	private final ClassModifiers modifiers;

	private final ClassType thisType, superType;
	private final @Immutable List<ClassType> interfaces;

	private final ClassInfo classinfo;

	// Суперкласс и интерфейсы, которые будут выведены в заголовок класса
	// Например, все enum классы наследуются от java.lang.Enum,
	// все аннотации реализуют интерфейс java.lang.annotation.Annotation и т.д.
	// Такие суперклассы и суперинтерфейсы не должны выводиться в заголовке класса.
	private final @Nullable Type visibleSuperType;
	private final @Immutable List<? extends Type> visibleInterfaces;

	private final @Immutable List<JavaField> fields, constants;
	private final @Nullable @Immutable List<JavaEnumField> enumConstants;
	private final @Nullable @Immutable List<JavaField> recordComponents;
	private final @Immutable List<JavaMethod> methods;

	private @Immutable List<JavaClass> innerClasses;

	private final Attributes attributes;
	private final Optional<ClassSignatureAttribute> signature;

	private final String fileName, sourceFileName;
	private final @Nullable String directory;


	private @Nullable Type getVisibleSuperType() {
		if (!thisType.isAnonymous()) {

			if (superType.equals(ClassType.OBJECT) || isRecord()) {
				return null;
			}

			if (modifiers.isInterface()) {
				throw new IllegalClassHeaderException("Interface cannot inherit from other class than java.lang.Object");
			}

			if (modifiers.isEnum() && superType.equals(ClassType.ENUM)) {
				return null;
			}
		}

		return signature.isPresent() ? signature.get().getSuperType() : superType;
	}


	private static final Predicate<ClassType> isNotAnnotation = interfaceType -> !interfaceType.equals(ClassType.ANNOTATION);

	private @Immutable List<? extends Type> getVisibleInterfaces() {

		var interfaces = signature.isPresent() ? signature.get().getInterfaces() : this.interfaces;

		if (!thisType.isAnonymous()) {

			if (modifiers.isNotAnnotation() || interfaces.stream().allMatch(isNotAnnotation))
				return interfaces;

			return interfaces.stream().filter(isNotAnnotation).toList();
		}

		return interfaces;
	}

	private String getFileName(Attributes attributes) {
		SourceFileAttribute sourceFileAttr = attributes.getNullable(AttributeType.SOURCE_FILE);

		return sourceFileAttr == null ?
				thisType.getTopLevelClass().getSimpleName() :
				sourceFileAttr.getSourceFileName().replaceFirst(".java$", "");
	}

	private String getSourceFileName(Attributes attributes) {
		SourceFileAttribute sourceFileAttr = attributes.getNullable(AttributeType.SOURCE_FILE);

		return sourceFileAttr == null ?
				thisType.getTopLevelClass().getSimpleName() + ".java" :
				sourceFileAttr.getSourceFileName();
	}


	private JavaClass(ExtendedDataInputStream in, PerformingType performingType, @Nullable String directory) {
		if (in.readInt() != SIGNATURE)
			throw new ClassFormatException("Illegal class header");

		this.version = Version.read(in);
		var pool = this.pool = ConstantPool.read(in);

		var modifiers = ClassModifiers.read(in);
		this.thisType = ClassType.fromConstant(pool.getClassConstant(in.readUnsignedShort()));
		this.superType = ClassType.fromNullableConstant(pool.getNullableClassConstant(in.readUnsignedShort()), ClassType.OBJECT);

		this.interfaces = in.readImmutableList(() -> pool.getClassConstant(in.readUnsignedShort()).toClassType());

		var classinfo = this.classinfo = ClassInfo.of(this, modifiers, performingType);

		this.fields = Collections.unmodifiableList(JavaField.readFields(in, classinfo, pool));
		this.methods = Collections.unmodifiableList(JavaMethod.readMethods(in, classinfo, pool));

		this.constants = fields.stream().filter(JavaField::isConstant).toList();
		this.enumConstants = modifiers.isEnum() ?
				fields.stream().filter(field -> field.getModifiers().isEnum()).map(field -> (JavaEnumField) field).toList() :
				null;

		this.recordComponents = isRecord() ?
				fields.stream().filter(field -> field.canStringifyAsRecordComponent(classinfo)).toList() :
				null;

		this.attributes = Attributes.read(in, pool, Location.CLASS);
		classinfo.setAttributes(attributes);


		if (thisType.isNested()) {

			InnerClassEntry innerClass = attributes.getOrDefaultEmpty(AttributeType.INNER_CLASSES).find(thisType);

			if (innerClass != null) {
				ClassModifiers innerClassModifiers = innerClass.getModifiers();

				if (innerClassModifiers.and(~(ACC_ACCESS_FLAGS | ACC_STATIC | ACC_SUPER)) !=
						modifiers.and(~(ACC_ACCESS_FLAGS | ACC_STATIC | ACC_SUPER))) {

					DecompilationContext.logWarning("modifiers of class " + thisType.getName()
							+ " are not matching to the modifiers in \"" + AttributeNames.INNER_CLASSES + "\" attribute:"
							+ modifiers.toHexWithPrefix() + ", " + innerClassModifiers.toHexWithPrefix());
				}

				modifiers = innerClassModifiers;
				classinfo.setModifiers(modifiers);
			}
		}

		this.modifiers = modifiers;


		this.signature = Optional.ofNullable(attributes.getNullable(AttributeType.CLASS_SIGNATURE));
		signature.ifPresent(classSignatureAttr -> classSignatureAttr.checkTypes(superType, interfaces));

		this.visibleSuperType = getVisibleSuperType();
		this.visibleInterfaces = getVisibleInterfaces();


		this.fileName = getFileName(attributes);
		this.sourceFileName = getSourceFileName(attributes);

		this.directory = directory;

		CLASSES.put(thisType, this);

		methods.forEach(method -> method.beforeDecompilation(classinfo));
	}

	public static JavaClass read(InputStream in, PerformingType performingType) {
		return read(in, performingType, null);
	}

	public static JavaClass read(InputStream in, PerformingType performingType, String directory) {
		return read(new ExtendedDataInputStream(in), performingType, directory);
	}

	public static JavaClass read(ExtendedDataInputStream in, PerformingType performingType) {
		return read(in, performingType, null);
	}

	public static JavaClass read(ExtendedDataInputStream in, PerformingType performingType, String directory) {
		return new JavaClass(in, performingType, directory);
	}


	private JavaClass(AssemblingInputStream in, @Nullable String directory) {
		this.modifiers = ClassModifiers.parse(in);
		this.thisType = in.nextClassType();

		this.superType = in.advanceIfHasNext("extends") ?
				in.nextClassType() :
				ClassType.OBJECT;

		this.interfaces = in.advanceIfHasNext("implements") ?
				in.nextClassTypesStream().toList() :
				Collections.emptyList();

		this.signature = Optional.empty();

		this.visibleSuperType = getVisibleSuperType();
		this.visibleInterfaces = getVisibleInterfaces();

		in.requireNext('{');

		this.version = Version.parse(in);

		this.pool = ConstantPool.newInstance();

		this.classinfo = ClassInfo.of(this, modifiers, PerformingType.ASSEMBLE);

		this.fields = JavaField.parseFields(in, classinfo, pool);
		this.constants = Collections.emptyList();
		this.enumConstants = null;
		this.recordComponents = null;

		this.methods = JavaMethod.parseMethods(in, classinfo, pool);

		this.innerClasses = Collections.emptyList();

		this.attributes = Attributes.empty();
		this.fileName = getFileName(attributes);
		this.sourceFileName = getSourceFileName(attributes);

		this.directory = directory;
	}

	public static @Nullable JavaClass parse(AssemblingInputStream in, @Nullable String directory) {
		return new JavaClass(in, directory);
	}

	public static @Nullable JavaClass find(ClassType type) {
		JavaClass javaClass = CLASSES.get(type);

		if (javaClass != null) {
			return javaClass;
		}

		if (type.isNested() && Yava.getConfig().canSearchNestedClasses()) {
			return loadNestedClass(type);
		}

		return null;
	}

	private static @Nullable JavaClass loadNestedClass(ClassType type) {
		JavaClass enclosingClass = CLASSES.get(type.getTopLevelClass());

		if (enclosingClass != null && enclosingClass.directory != null) {

			return Yava.getInstance().getPerforming()
					.readSafe(enclosingClass.directory + separatorChar +
							type.getFullSimpleName().replace('.', '$') + ".class");
		}

		return null;
	}


	public Version getVersion() {
		return version;
	}

	public ConstantPool getConstPool() {
		return pool;
	}

	@Override
	public ClassModifiers getModifiers() {
		return modifiers;
	}


	public boolean isNested() {
		return thisType.isNested();
	}

	public boolean isAnonymous() {
		return thisType.isAnonymous();
	}

	public boolean isSealed() {
		return attributes.has(AttributeType.PERMITTED_SUBCLASSES);
	}

	public boolean isRecord() {
		return superType.equals(ClassType.RECORD);
	}


	public ClassType getThisType() {
		return thisType;
	}

	public ClassType getSuperType() {
		return superType;
	}

	public @Immutable List<ClassType> getInterfaces() {
		return interfaces;
	}

	public ClassInfo getClassInfo() {
		return classinfo;
	}


	public @Immutable List<JavaField> getFields() {
		return fields;
	}

	public @Immutable List<JavaField> getConstants() {
		return constants;
	}

	public @Nullable @Immutable List<JavaField> getRecordComponents() {
		return recordComponents;
	}

	public @Nullable @Immutable List<JavaEnumField> getEnumConstants() {
		return enumConstants;
	}

	public @Immutable List<JavaMethod> getMethods() {
		return methods;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public Optional<ClassSignatureAttribute> getSignature() {
		return signature;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public String getSourceFilePath() {
		return directory == null ?
				sourceFileName :
				directory + separatorChar + sourceFileName;
	}

	public String getFilePath(String extension) {
		return directory == null ?
				fileName + extension :
				directory + separatorChar + fileName + extension;
	}


	private final DecompilationStageHolder stageHolder = new DecompilationStageHolder(DecompilationStage.DISASSEMBLED);


	public void decompile(DecompilationStats stats) {
		stageHolder.nextStage(this, DecompilationStage.DISASSEMBLED, DecompilationStage.DECOMPILED);

		methods.forEach(method -> method.decompile(classinfo, stats));

		if (enumConstants != null)
			enumConstants.forEach(JavaEnumField::checkHasEnumInitializer);


		InnerClassesAttribute innerClassesAttribute = attributes.getNullable(AttributeType.INNER_CLASSES);

		if (innerClassesAttribute != null) {
			innerClasses = innerClassesAttribute.getEntryStreamWithOuterType(thisType)
					.map(entry -> find(entry.getInnerType()))
					.filter(Objects::nonNull).toList();

			innerClasses.forEach(innerClass -> innerClass.decompile(stats));

		} else {
			innerClasses = Collections.emptyList();
		}

		stats.addMethodsStat(this, methods.stream().filter(JavaMethod::hasDecompilationException).toList(), methods.size());
	}

	public void afterDecompilation() {
		stageHolder.nextStage(this, DecompilationStage.DECOMPILED, DecompilationStage.AFTER_DECOMPILATION);
		methods.forEach(JavaMethod::afterDecompilation);
		innerClasses.forEach(JavaClass::afterDecompilation);
	}

	public void resolveImports() {
		addImports(classinfo);
		classinfo.uniqueImports();
	}

	@Override
	public void addImports(ClassInfo otherClassinfo) {
		stageHolder.nextStage(this, DecompilationStage.AFTER_DECOMPILATION, DecompilationStage.IMPORTS_RESOLVED);

		final ClassInfo classinfo;

		if (thisType.isAnonymous()) {
			this.classinfo.bindEnvironmentTo(otherClassinfo);
			classinfo = this.classinfo;
		} else {
			classinfo = otherClassinfo;
		}

		classinfo.addImportIfNotNull(visibleSuperType);
		classinfo.addImportsFor(visibleInterfaces);
		attributes.addImports(classinfo);
		classinfo.addImportsFor(fields);
		classinfo.addImportsFor(methods);
		innerClasses.forEach(innerClass -> {
			innerClass.classinfo.bindEnvironmentTo(classinfo);
			innerClass.resolveImports();
		});
	}


	@Override
	public String toString() {
		return toString(modifiers, thisType, superType, interfaces);
	}


	public static String toString(ClassModifiers modifiers, RealReferenceType thisType,
								  RealReferenceType superType, List<? extends RealReferenceType> interfaces) {

		return modifiers.toSimpleString() + ' ' + thisType + " extends " + (superType == null ? "null" : superType.getName()) +
				(interfaces.isEmpty() ? "" :
						interfaces.stream().map(Type::getName).collect(Collectors.joining(", ", " implements ", "")));
	}


	public boolean canStringifyAsInnerClass() {
		return super.canStringify(classinfo);
	}


	@Override
	public boolean canStringify(ClassInfo classinfo) {
		if (thisType.isSpecialClassType()) {
			// У package-info есть флаг ACC_SYNTHETIC, даже если этот класс есть в исходном коде
			return super.canStringify(classinfo) || thisType.isPackageInfo();
		}

		if (!super.canStringify(classinfo)) {
			return false;
		}

		if (thisType.isAnonymous()) {
			return !CLASSES.containsKey(thisType.getEnclosingClass());
		}

		InnerClassEntry innerClass =
				attributes.getOrDefaultEmpty(AttributeType.INNER_CLASSES).find(thisType);

		return innerClass == null || !CLASSES.containsKey(
				Objects.requireNonNullElse(innerClass.getOuterType(), thisType.getEnclosingClass())
		);
	}

	public boolean canStringify() {
		return canStringify(classinfo);
	}


	public void writeTo(StringifyOutputStream out) {
		writeTo(out, classinfo);
	}

	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {

		stageHolder.checkStage(this, DecompilationStage.IMPORTS_RESOLVED, DecompilationStage.WRITTEN);

		if (Yava.getConfig().printClassVersion())
			out.print("/* Java version: ").print(version.toString()).println(" */");


		if (thisType.isPackageInfo()) {
			writeAsPackageInfo(out, classinfo);

		} else if (thisType.isModuleInfo()) {
			writeAsModuleInfo(out, classinfo);

		} else {
			writeAsClass(out, classinfo);
		}
	}


	private void writeAsModuleInfo(StringifyOutputStream out, ClassInfo classinfo) {
		checkHasNoMembers();

		ModuleAttribute moduleAttribute = attributes.getOrThrow(AttributeType.MODULE, () -> new DecompilationException("module-info haven't \"Module\" attribute"));
		moduleAttribute.writeTo(out, classinfo);
	}


	private void writeAsPackageInfo(StringifyOutputStream out, ClassInfo classinfo) {
		checkHasNoMembers();

		writeAnnotations(out, classinfo, attributes);
		writePackage(out);
		classinfo.writeImports(out, false);
	}


	private void checkHasNoMembers() {
		if (!fields.isEmpty())
			throw new DecompilationException(thisType + " cannot have any fields");

		if (!methods.isEmpty())
			throw new DecompilationException(thisType + " cannot have any methods");
	}


	private void writeAsClass(StringifyOutputStream out, ClassInfo classinfo) {
		writePackage(out);
		classinfo.writeImports(out);
		writeAsInnerClass(out, classinfo);
	}


	private void writeAsInnerClass(StringifyOutputStream out) {
		writeAsInnerClass(out, classinfo);
	}

	private void writeAsInnerClass(StringifyOutputStream out, ClassInfo classinfo) {

		writeAnnotations(out, classinfo, attributes);

		writeHeader(out, classinfo);

		// Мы можем опустить указание класса только внутри тела класса, поэтому этот метод вызывается здесь
		classinfo.enterClassScope(thisType);

		writeBody(out, classinfo);

		classinfo.leaveClassScope(thisType);
	}


	private void writePackage(StringifyOutputStream out) {
		if (!thisType.getPackageName().isEmpty())
			out.print("package ").print(thisType.getPackageName()).println(';').println();
	}

	private void writeHeader(StringifyOutputStream out, ClassInfo classinfo) {

		out.printIndent().print(modifiersToString(), classinfo).print(thisType.getSimpleName());

		if (signature.isPresent()) {
			var parameters = signature.get().getParameters();

			if (!parameters.isEmpty())
				out.print(parameters, classinfo);
		}

		if (isRecord()) {
			out.print('(')
					.printAllUsingFunction(recordComponents, field -> field.writeWithoutSemicolon(out, classinfo), ", ")
					.print(')');
		}

		if (visibleSuperType != null) {
			out.print(" extends ").print(visibleSuperType, classinfo);
		}

		if (!visibleInterfaces.isEmpty()) {
			out.print(modifiers.isInterface() ? " extends " : " implements ")
					.printAll(visibleInterfaces, classinfo, ", ");
		}

		out.printIfNotNull(attributes.getNullable(AttributeType.PERMITTED_SUBCLASSES), classinfo);
	}

	@Override
	public String getModifiersTarget() {
		return "class " + thisType.getName();
	}

	private IWhitespaceStringBuilder modifiersToString() {
		IWhitespaceStringBuilder str = new WhitespaceStringBuilder().printTrailingSpace();

		var modifiers = this.modifiers;

		accessModifiersToString(modifiers, str);

		if (thisType.isNested()) {

			if (modifiers.isEnum() || modifiers.isInterface()) {
				if (!modifiers.isStatic())
					throw new IllegalModifiersException(this, modifiers,
							"nested " + (modifiers.isEnum() ? "enum" : "interface") + " cannot be non-static");

			} else {
				if (modifiers.isStatic()) {
					str.append("static");
				}
			}

		} else {
			if (modifiers.isStatic()) {
				throw new IllegalModifiersException(this, modifiers, "non-nested class cannot be static");
			}
		}

		if (modifiers.isSynthetic()) {
			str.append("/* synthetic */");
		}

		if (modifiers.isStrictfp()) {
			str.append("strictfp");
		}

		if (attributes.has(AttributeType.PERMITTED_SUBCLASSES)) {
			if (modifiers.isFinal())
				throw new IllegalModifiersException(this, modifiers, "sealed class cannot be final");

			str.append("sealed");

		} else if (modifiers.isNotFinal()) {

			JavaClass superclass = find(superType);

			if (superclass != null && superclass.isSealed()) {
				str.append("non-sealed");
			}
		}

		switch (modifiers.and(ACC_FINAL | ACC_ABSTRACT | ACC_INTERFACE | ACC_ANNOTATION | ACC_ENUM)) {
			case ACC_NONE -> str.append("class");
			case ACC_FINAL -> str.append("final class");
			case ACC_ABSTRACT -> str.append("abstract class");
			case ACC_ABSTRACT | ACC_INTERFACE -> str.append("interface");
			case ACC_ABSTRACT | ACC_INTERFACE | ACC_ANNOTATION -> str.append("@interface");
			case ACC_ENUM, ACC_FINAL | ACC_ENUM, ACC_ABSTRACT | ACC_ENUM -> str.append("enum");
			default -> throw new IllegalModifiersException(this, modifiers);
		}

		return str;
	}


	private void writeBody(StringifyOutputStream out, ClassInfo classinfo) {

		out.print(" {").increaseIndent();

		var stringableFields = fields.stream().filter(field -> field.canStringify(classinfo)).toList();
		var stringableMethods = methods.stream().filter(method -> method.canStringify(classinfo)).toList();
		var stringableClasses = innerClasses.stream().filter(JavaClass::canStringifyAsInnerClass).toList();

		boolean canWriteSomething = !stringableFields.isEmpty() || !stringableMethods.isEmpty() || !stringableClasses.isEmpty();

		if (canWriteSomething && enumConstants != null && enumConstants.isEmpty()) {
			out.println().printIndent().print(';');
		}

		boolean hasEnumConstants = enumConstants != null && !enumConstants.isEmpty();

		if (hasEnumConstants) {
			writeEnumConstants(out, classinfo);
			out.print(canWriteSomething ? ';' : '\n');
		}


		if (!stringableFields.isEmpty())
			out.println().println();
		else if (!stringableMethods.isEmpty())
			out.println();

		writeFields(stringableFields, out, classinfo);
		writeMethods(stringableMethods, out, classinfo);
		writeInnerClasses(stringableClasses, out);

		out.reduceIndent();

		if (canWriteSomething | hasEnumConstants)
			out.printIndent();

		out.write('}');
	}


	private void writeEnumConstants(StringifyOutputStream out, ClassInfo classinfo) {
		out.println().printIndent();

		LoopUtil.forEachPair(enumConstants,
				enumConstant -> enumConstant.writeNameAndInitializer(out, classinfo),
				(enumConstant1, enumConstant2) -> enumConstant1.writeIndent(out, enumConstant2));
	}


	private static void writeFields(List<JavaField> fields, StringifyOutputStream out, ClassInfo classinfo) {

		JavaField prevField = null; // NullPointerException никогда не возникнет для этой переменной

		for (JavaField field : fields) {

			if (prevField != null) {

				if (field.canJoinDeclaration(prevField)) {
					out.print(", ").printUsingFunction(field, classinfo, JavaField::writeNameAndInitializer);
					continue;

				} else {
					out.println(';');
					if (!field.getModifiers().equals(prevField.getModifiers()) ||
							!field.getDescriptor().getType().equals(prevField.getDescriptor().getType())) {
						out.println();
					}
				}
			}

			field.writeWithoutSemicolon(out, classinfo);
			prevField = field;
		}

		if (prevField != null)
			out.println(';');
	}

	private static void writeMethods(List<JavaMethod> methods, StringifyOutputStream out, ClassInfo classinfo) {
		out.printEachUsingFunction(methods, method -> out.println().print(method, classinfo));
	}

	private static void writeInnerClasses(List<JavaClass> innerClasses, StringifyOutputStream out) {
		innerClasses.forEach(innerClass -> out.println().println().printUsingFunction(innerClass, JavaClass::writeAsInnerClass).println());
	}


	public void writeAsNewAnonymousObject(StringifyOutputStream out, StringifyContext context, InvokespecialOperation invokeOperation) {

		stageHolder.checkStage(this, DecompilationStage.IMPORTS_RESOLVED, DecompilationStage.WRITTEN);

		if (visibleInterfaces.size() > 1 || visibleInterfaces.size() == 1 && !visibleSuperType.equals(ClassType.OBJECT)) {
			throw new DecompilationException("Anonymous class cannot extend more than one class or implement more than one interface");
		}

		out.printsp("new").print(visibleInterfaces.size() == 1 ? visibleInterfaces.get(0) : visibleSuperType, classinfo);
		invokeOperation.writeArguments(out, context);

		writeBody(out, classinfo);
	}


	public void writeDisassembled(DisassemblingOutputStream out) {
		writeDisassembled(out, classinfo);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		out .print(modifiersToString(), classinfo)
			.print(thisType, classinfo);

		if (!superType.equals(ClassType.OBJECT))
			out .print(" extends ")
				.print(superType, classinfo);

		if (!interfaces.isEmpty())
			out .print(" implements ")
				.printAll(interfaces, classinfo, ", ");

		out .println(" {").increaseIndent()
			.printIndent().println(version, classinfo).println();

		if (!fields.isEmpty()) {
			out .printIndent().println("fields {").increaseIndent()
				.printAll(fields, classinfo, "")
				.reduceIndent().printIndent().println('}').println();
		}

		if (!methods.isEmpty()) {
			out .printIndent().println("methods {").increaseIndent()
				.printAll(methods, classinfo, "")
				.reduceIndent().printIndent().println('}');
		}

		out.reduceIndent().println('}');
	}


	@Override
	public void serialize(AssemblingOutputStream out) {
		var buffer = new ByteArrayOutputStream();

		AssemblingOutputStream bufferOut = new AssemblingOutputStream(buffer);

		bufferOut.record(modifiers)
			.recordShort(pool.classIndexFor(thisType))
			.recordShort(pool.classIndexFor(superType))
			.recordShorts(interfaces.stream().mapToInt(pool::classIndexFor).toArray())
			.recordAll(fields, pool)
			.recordAll(methods, pool)
			.record(attributes, pool)
			.close();

		// Мы не можем записать pool сразу, так как при записи всего остального
		// pool может быть изменён. Поэтому сначала мы запишем всё в буфер,
		// а затем запишем pool и буфер

		out .recordInt(SIGNATURE)
			.record(version)
			.record(pool)
			.writeByteArray(buffer.toByteArray());
	}
}
