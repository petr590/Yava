package x590.jdecompiler.clazz;

import static x590.jdecompiler.modifiers.Modifiers.*;
import static java.io.File.separatorChar;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import x590.jdecompiler.JavaClassElement;
import x590.jdecompiler.DecompilationStage;
import x590.jdecompiler.DecompilationStage.DecompilationStageHolder;
import x590.jdecompiler.attribute.AttributeNames;
import x590.jdecompiler.attribute.AttributeType;
import x590.jdecompiler.attribute.Attributes;
import x590.jdecompiler.attribute.InnerClassesAttribute;
import x590.jdecompiler.attribute.InnerClassesAttribute.InnerClassEntry;
import x590.jdecompiler.attribute.ModuleAttribute;
import x590.jdecompiler.attribute.SourceFileAttribute;
import x590.jdecompiler.attribute.Attributes.Location;
import x590.jdecompiler.attribute.signature.ClassSignatureAttribute;
import x590.jdecompiler.constpool.ConstantPool;
import x590.jdecompiler.context.DecompilationContext;
import x590.jdecompiler.context.StringifyContext;
import x590.jdecompiler.exception.ClassFormatException;
import x590.jdecompiler.exception.DecompilationException;
import x590.jdecompiler.exception.IllegalClassHeaderException;
import x590.jdecompiler.exception.IllegalModifiersException;
import x590.jdecompiler.field.JavaEnumField;
import x590.jdecompiler.field.JavaField;
import x590.jdecompiler.io.DisassemblingOutputStream;
import x590.jdecompiler.io.ExtendedDataInputStream;
import x590.jdecompiler.io.ExtendedDataOutputStream;
import x590.jdecompiler.io.StringifyOutputStream;
import x590.jdecompiler.main.JDecompiler;
import x590.jdecompiler.method.JavaMethod;
import x590.jdecompiler.modifiers.ClassModifiers;
import x590.jdecompiler.operation.invoke.InvokespecialOperation;
import x590.jdecompiler.type.ClassType;
import x590.jdecompiler.type.Type;
import x590.jdecompiler.util.WhitespaceStringBuilder;
import x590.jdecompiler.util.IWhitespaceStringBuilder;
import x590.util.BooleanHolder;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;

/**
 * Описывает декомпилируемый класс
 */
public final class JavaClass extends JavaClassElement {
	
	private static final Map<ClassType, JavaClass> classes = new HashMap<>();
	
	
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
	private final @Immutable List<JavaMethod> methods;
	
	private @Immutable List<JavaClass> innerClasses;
	
	private final Attributes attributes;
	private final @Nullable ClassSignatureAttribute signature;
	
	private final String sourceFileName;
	private final @Nullable String directory;
	
	
	private @Nullable Type getVisibleSuperType() {
		if(!thisType.isAnonymous()) {
			
			if(superType.equals(ClassType.OBJECT)) {
				return null;
			}
			
			if(modifiers.isInterface()) {
				throw new IllegalClassHeaderException("Interface cannot inherit from other class than java.lang.Object");
			}
			
			if(modifiers.isEnum() && superType.equals(ClassType.ENUM)) {
				return null;
			}
		}
		
		return signature != null ? signature.superType : superType;
	}
	
	
	private static final Predicate<ClassType> isNotAnnotation = interfaceType -> !interfaceType.equals(ClassType.ANNOTATION);
	
	private @Immutable List<? extends Type> getVisibleInterfaces() {
		
		var interfaces = signature != null ? signature.interfaces : this.interfaces;
		
		if(!thisType.isAnonymous()) {
			
			if(modifiers.isNotAnnotation() || interfaces.stream().allMatch(isNotAnnotation))
				return interfaces;
			
			return interfaces.stream().filter(isNotAnnotation).toList();
		}
		
		return interfaces;
	}
	
	
	private JavaClass(ExtendedDataInputStream in, @Nullable String directory) {
		if(in.readInt() != 0xCAFEBABE)
			throw new ClassFormatException("Illegal class header");
		
		this.version = Version.read(in);
		this.pool = ConstantPool.read(in);
		var pool = this.pool;
		
		this.modifiers = ClassModifiers.read(in);
		this.thisType = ClassType.fromConstant(pool.getClassConstant(in.readUnsignedShort()));
		this.superType = ClassType.fromNullableConstant(pool.getNullableClassConstant(in.readUnsignedShort()), ClassType.OBJECT);
		
		this.interfaces = in.readImmutableList(() -> pool.getClassConstant(in.readUnsignedShort()).toClassType());
		
		this.classinfo = new ClassInfo(this, version, pool, modifiers, thisType, superType, interfaces);
		
		this.fields = Collections.unmodifiableList(JavaField.readFields(in, classinfo, pool));
		this.methods = Collections.unmodifiableList(JavaMethod.readMethods(in, classinfo, pool));
		
		this.constants = fields.stream().filter(JavaField::isConstant).toList();
		this.enumConstants = modifiers.isEnum() ? fields.stream().filter(field -> field.getModifiers().isEnum()).map(field -> (JavaEnumField)field).toList() : null;
		
		this.attributes = Attributes.read(in, pool, Location.CLASS);
		classinfo.setAttributes(attributes);
		
		this.signature = attributes.getNullable(AttributeType.CLASS_SIGNATURE);
		if(signature != null)
			signature.checkTypes(superType, interfaces);
		
		this.visibleSuperType = getVisibleSuperType();
		this.visibleInterfaces = getVisibleInterfaces();
		
		SourceFileAttribute sourceFileAttr = attributes.getNullable(AttributeType.SOURCE_FILE);
		
		this.sourceFileName = sourceFileAttr == null ? thisType.getTopLevelClass().getSimpleName() + ".java" : sourceFileAttr.getSourceFileName();
		this.directory = directory;
		
		classes.put(thisType, this);
	}
	
	public static JavaClass read(InputStream in) {
		return read(in, null);
	}
	
	public static JavaClass read(InputStream in, String directory) {
		return read(new ExtendedDataInputStream(in), directory);
	}
	
	public static JavaClass read(ExtendedDataInputStream in) {
		return read(in, null);
	}
	
	public static JavaClass read(ExtendedDataInputStream in, String directory) {
		return new JavaClass(in, directory);
	}
	
	// TODO someday
	public static @Nullable JavaClass parse(Reader in) {
		return null;
	}
	
	public static @Nullable JavaClass find(ClassType type) {
		JavaClass javaClass = classes.get(type);
		
		if(javaClass != null) {
			return javaClass;
		}
		
		if(type.isNested() && JDecompiler.getConfig().canSearchNestedClasses()) {
			return loadAnonymousClass(type);
		}
		
		return null;
	}
	
	private static final @Nullable JavaClass loadAnonymousClass(ClassType type) {
		JavaClass enclosingClass = classes.get(type.getTopLevelClass());
		
		if(enclosingClass != null && enclosingClass.directory != null) {
			
			return JDecompiler.getInstance().getPerforming()
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
	
	public @Nullable @Immutable List<JavaEnumField> getEnumConstants() {
		return enumConstants;
	}
	
	public @Immutable List<JavaMethod> getMethods() {
		return methods;
	}
	
	public Attributes getAttributes() {
		return attributes;
	}
	
	public String getSourceFileName() {
		return sourceFileName;
	}
	
	public String getSourceFilePath() {
		return directory == null ?
				sourceFileName :
				directory + separatorChar + getSourceFileName();
	}
	
	
	private final DecompilationStageHolder stageHolder = new DecompilationStageHolder(DecompilationStage.DISASSEMBLED);
	
	
	public void decompile() {
		stageHolder.nextStage(DecompilationStage.DISASSEMBLED, DecompilationStage.DECOMPILED);
		
		methods.forEach(method -> method.decompile(classinfo, pool));
		
		if(enumConstants != null)
			enumConstants.forEach(enumConstant -> enumConstant.checkHasEnumInitializer(classinfo));
		
		
		InnerClassesAttribute innerClassesAttribute = attributes.getNullable(AttributeType.INNER_CLASSES);
		
		if(innerClassesAttribute != null) {
			innerClasses = innerClassesAttribute.getEntries().values().stream()
					.filter(entry -> entry.hasOuterType() && entry.getOuterType().equals(thisType))
					.map(entry -> find(entry.getInnerType()))
					.filter(innerClass -> innerClass != null).toList();
			
			innerClasses.forEach(JavaClass::decompile);
			
		} else {
			innerClasses = Collections.emptyList();
		}
	}
	
	public void resolveImports() {
		addImports(classinfo);
		classinfo.uniqImports();
	}
	
	@Override
	public void addImports(ClassInfo otherClassinfo) {
		stageHolder.nextStage(DecompilationStage.DECOMPILED, DecompilationStage.IMPORTS_RESOLVED);
		
		final ClassInfo classinfo;
		
		if(thisType.isAnonymous()) {
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
		return modifiers + " " + thisType + " extends " + superType.getName() +
				(interfaces.isEmpty() ? "" :
					interfaces.stream().map(Type::getName).collect(Collectors.joining(", ", " implements ", "")));
	}
	
	
	public boolean canStringifyAsInnerClass() {
		return super.canStringify(classinfo);
	}
	
	
	@Override
	public boolean canStringify(ClassInfo classinfo) {
		if(thisType.isSpecialClassType()) {
			// У package-info есть флаг ACC_SYNTHETIC, даже если этот класс есть в исходном коде
			return super.canStringify(classinfo) || thisType.isPackageInfo();
		}
		
		if(!super.canStringify(classinfo)) {
			return false;
		}
		
		if(thisType.isAnonymous()) {
			return !classes.containsKey(thisType.getEnclosingClass());
		}
		
		InnerClassEntry innerClass =
				attributes.getOrDefault(AttributeType.INNER_CLASSES, InnerClassesAttribute.empty()).find(thisType);
		
		return innerClass == null || !classes.containsKey(
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
		
		stageHolder.checkStage(DecompilationStage.IMPORTS_RESOLVED, DecompilationStage.WRITTEN);
		
		if(JDecompiler.getConfig().printClassVersion())
			out.print("/* Java version: ").print(version.toString()).println(" */");
		
		
		if(thisType.isPackageInfo()) {
			writeAsPackageInfo(out, classinfo);
			
		} else if(thisType.isModuleInfo()) {
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
		if(!fields.isEmpty())
			throw new DecompilationException(thisType + " cannot have any fields");
		
		if(!methods.isEmpty())
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
		
		// Мы можем опустить указание класса только внутри тела класса, поэтому этот метод вызывается здесь
		classinfo.enterClassScope(thisType);
		
		writeHeader(out, classinfo);
		
		writeBody(out, classinfo);
		
		classinfo.leaveClassScope(thisType);
	}
	
	
	private void writePackage(StringifyOutputStream out) {
		if(!thisType.getPackageName().isEmpty())
			out.print("package ").print(thisType.getPackageName()).println(';').println();
	}
	
	private void writeHeader(StringifyOutputStream out, ClassInfo classinfo) {
		
		out .printIndent().print(modifiersToString(classinfo), classinfo).print(thisType.getSimpleName())
			.printIfNotNull(attributes.getNullable(AttributeType.PERMITTED_SUBCLASSES), classinfo);
		
		if(signature != null) {
			out.printIfNotNull(signature.parameters, classinfo);
		}
		
		if(visibleSuperType != null) {
			out.print(" extends ").print(visibleSuperType, classinfo);
		}
		
		if(!visibleInterfaces.isEmpty()) {
			out .print(modifiers.isInterface() ? " extends " : " implements ")
				.printAll(visibleInterfaces, classinfo, ", ");
		}
	}
	
	@Override
	public String getModifiersTarget() {
		return "class " + thisType.getName();
	}
	
	private IWhitespaceStringBuilder modifiersToString(ClassInfo classinfo) {
		IWhitespaceStringBuilder str = new WhitespaceStringBuilder().printTrailingSpace();
		
		var modifiers = this.modifiers;
		
		if(thisType.isNested()) {
			
			InnerClassesAttribute innerClasses = attributes.getNullable(AttributeType.INNER_CLASSES);
			if(innerClasses != null) {
				
				InnerClassEntry innerClass = innerClasses.find(thisType);
				if(innerClass != null) {
					ClassModifiers innerClassModifiers = innerClass.getModifiers();
					
					if(innerClassModifiers.and(~(ACC_ACCESS_FLAGS | ACC_STATIC | ACC_SUPER)) != (modifiers.and(~(ACC_ACCESS_FLAGS | ACC_STATIC | ACC_SUPER)))) {
						DecompilationContext.logWarning("modifiers of class " + thisType.getName()
								+ " are not matching to the modifiers in \"" + AttributeNames.INNER_CLASSES + "\" attribute:"
								+ innerClassModifiers.toHexWithPrefix() + ", " + modifiers.toHexWithPrefix());
					}
					
					modifiers = innerClassModifiers;
				}
			}
		}
		
		baseModifiersToString(str);
		
		if(modifiers.isStrictfp()) {
			str.append("strictfp");
		}
		
		if(attributes.has(AttributeType.PERMITTED_SUBCLASSES)) {
			if(modifiers.isFinal())
				throw new IllegalModifiersException(this, modifiers, "sealed class cannot be final");
			
			str.append("sealed");
		}
		
		switch(modifiers.and(ACC_FINAL | ACC_ABSTRACT | ACC_INTERFACE | ACC_ANNOTATION | ACC_ENUM)) {
			case ACC_NONE ->
				str.append("class");
			case ACC_FINAL ->
				str.append("final class");
			case ACC_ABSTRACT ->
				str.append("abstract class");
			case ACC_ABSTRACT | ACC_INTERFACE ->
				str.append("interface");
			case ACC_ABSTRACT | ACC_INTERFACE | ACC_ANNOTATION ->
				str.append("@interface");
			case ACC_ENUM, ACC_FINAL | ACC_ENUM, ACC_ABSTRACT | ACC_ENUM ->
				str.append("enum");
			default ->
				throw new IllegalModifiersException(this, modifiers);
		}
		
		return str;
	}
	
	
	private void writeBody(StringifyOutputStream out, ClassInfo classinfo) {

		out.print(" {").increaseIndent();
		
		if(enumConstants != null) {
			writeEnumConstants(out, classinfo);
		}
		
		List<JavaField> stringableFields = fields.stream().filter(field -> field.canStringify(classinfo)).toList();
		List<JavaMethod> stringableMethods = methods.stream().filter(method -> method.canStringify(classinfo)).toList();
		
		
		if(!stringableFields.isEmpty())
			out.println().println();
		else if(!stringableMethods.isEmpty())
			out.println();
		
		writeFields(stringableFields, out, classinfo);
		writeMethods(stringableMethods, out, classinfo);
		
		var wroteSomething = new BooleanHolder(!stringableFields.isEmpty() || !stringableMethods.isEmpty());
		
		innerClasses.stream()
				.filter(JavaClass::canStringifyAsInnerClass)
				.forEach(innerClass -> {
						wroteSomething.set(true);
						out.println().printUsingFunction(innerClass, JavaClass::writeAsInnerClass).println();
					});
		
		out.reduceIndent();
		
		if(wroteSomething.isTrue())
			out.printIndent();
		
		out.write('}');
	}
	
	
	private void writeEnumConstants(StringifyOutputStream out, ClassInfo classinfo) {
		out .println().printIndent()
			.printAllUsingFunction(enumConstants,
				enumConstant -> enumConstant.writeNameAndInitializer(out, classinfo),
				enumConstant -> enumConstant.writeIndent(out, classinfo)
			).println(';');
	}
	
	
	private static void writeFields(List<JavaField> fields, StringifyOutputStream out, ClassInfo classinfo) {
		
		JavaField prevField = null; // NullPointerException никогда не возникнет для этой переменной
		
		for(JavaField field : fields) {
			
			if(prevField != null) {
				
				if(field.canJoinDeclaration(prevField)) {
					out.print(", ").printUsingFunction(field, classinfo, JavaField::writeNameAndInitializer);
					continue;
					
				} else {
					out.println(';');
					if(!field.getDescriptor().getType().equals(prevField.getDescriptor().getType())) {
						out.println();
					}
				}
			}
			
			field.writeWithoutSemicolon(out, classinfo);
			prevField = field;
		}
		
		if(prevField != null)
			out.println(';');
	}
	
	private static void writeMethods(List<JavaMethod> methods, StringifyOutputStream out, ClassInfo classinfo) {
		out.printEachUsingFunction(methods, method -> out.println().print(method, classinfo));
	}
	
	
	public void writeAsNewAnonymousObject(StringifyOutputStream out, StringifyContext context, InvokespecialOperation invokeOperation) {
		
		stageHolder.checkStage(DecompilationStage.IMPORTS_RESOLVED, DecompilationStage.WRITTEN);
		
		if(visibleInterfaces.size() > 1 || visibleInterfaces.size() == 1 && !visibleSuperType.equals(ClassType.OBJECT)) {
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
		out .print(modifiersToString(classinfo), classinfo)
			.print(thisType, classinfo)
			.print(" extends ")
			.print(superType, classinfo);
		
		if(!interfaces.isEmpty())
			out .print(" implements ")
				.printAll(interfaces, classinfo, ", ");
		
		out .print(" {")
			.printAll(fields, classinfo)
			.printAll(methods, classinfo)
			.print('}');
	}
	
	
	@Override
	public void serialize(ExtendedDataOutputStream out) {
		
	}
}