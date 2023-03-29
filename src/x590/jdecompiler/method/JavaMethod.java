package x590.jdecompiler.method;

import static x590.jdecompiler.modifiers.Modifiers.*;

import java.util.ArrayList;
import java.util.List;

import x590.jdecompiler.JavaClassElement;
import x590.jdecompiler.attribute.AttributeType;
import x590.jdecompiler.attribute.Attributes;
import x590.jdecompiler.attribute.CodeAttribute;
import x590.jdecompiler.attribute.EmptyCodeAttribute;
import x590.jdecompiler.attribute.ExceptionsAttribute;
import x590.jdecompiler.attribute.Attributes.Location;
import x590.jdecompiler.attribute.signature.MethodSignatureAttribute;
import x590.jdecompiler.clazz.ClassInfo;
import x590.jdecompiler.clazz.IClassInfo;
import x590.jdecompiler.constpool.ConstantPool;
import x590.jdecompiler.context.DecompilationContext;
import x590.jdecompiler.context.DisassemblerContext;
import x590.jdecompiler.context.StringifyContext;
import x590.jdecompiler.exception.DecompilationException;
import x590.jdecompiler.exception.IllegalModifiersException;
import x590.jdecompiler.io.DisassemblingOutputStream;
import x590.jdecompiler.io.ExtendedDataInputStream;
import x590.jdecompiler.io.ExtendedDataOutputStream;
import x590.jdecompiler.io.StringifyOutputStream;
import x590.jdecompiler.main.JDecompiler;
import x590.jdecompiler.modifiers.MethodModifiers;
import x590.jdecompiler.operation.Operation;
import x590.jdecompiler.operation.load.LoadOperation;
import x590.jdecompiler.scope.MethodScope;
import x590.jdecompiler.type.ArrayType;
import x590.jdecompiler.type.ClassType;
import x590.jdecompiler.type.PrimitiveType;
import x590.jdecompiler.type.Type;
import x590.jdecompiler.util.IWhitespaceStringBuilder;
import x590.jdecompiler.util.WhitespaceStringBuilder;
import x590.util.Logger;
import x590.util.annotation.Nullable;
import x590.util.lazyloading.LazyLoadingBooleanValue;

public class JavaMethod extends JavaClassElement {
	
	private final MethodModifiers modifiers;
	private final MethodDescriptor descriptor;
	
	private final Attributes attributes;
	private final CodeAttribute codeAttribute;
	private final @Nullable MethodSignatureAttribute signature;
	
	private DisassemblerContext disassemblerContext;
	private @Nullable DecompilationContext decompilationContext;
	private StringifyContext stringifyContext;
	
	private MethodScope methodScope;
	
	private @Nullable MethodInfo methodInfo;
	
	private @Nullable String exceptionMessage;
	
	private boolean hasOverrideAttribute;
	
	private boolean isAutogenerated(ClassInfo classinfo) {
		
		if(signature != null && signature.hasGenericTypes() || attributes.has(AttributeType.EXCEPTIONS) ||
			attributes.has(AttributeType.RUNTIME_VISIBLE_ANNOTATIONS) || attributes.has(AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS)) {
			return false;
		}
		
		var hasNoOtherConstructors =
				new LazyLoadingBooleanValue(() -> !classinfo.hasMethod(method -> method != this && method.descriptor.isConstructor()));
		
		var descriptor = this.descriptor;
		var thisClassType = classinfo.getThisType();
		
		if(descriptor.isConstructorOf(thisClassType) &&
				// constructor by default
				(descriptor.argumentsEmpty() && modifiers.and(ACC_ACCESS_FLAGS) == classinfo.getModifiers().and(ACC_ACCESS_FLAGS)
						&& methodScope.isEmpty() && hasNoOtherConstructors.getAsBoolean() ||
				// anonymous class constructor
				thisClassType.isAnonymous())
		) {
			return true;
		}
		
		if(descriptor.isStaticInitializer() && methodScope.isEmpty()) { // empty static {}
			return true;
		}
		
		if(classinfo.getModifiers().isEnum()) {
			
			// enum constructor by default
			if(descriptor.isConstructorOf(thisClassType) && descriptor.argumentsEquals(ClassType.STRING, PrimitiveType.INT)
				&& modifiers.isPrivate() && methodScope.isEmpty() && hasNoOtherConstructors.getAsBoolean()) {
				
				return true;
			}
				
			if( descriptor.equalsIgnoreClass("valueOf", thisClassType, ClassType.STRING) || // Enum valueOf(String name)
				descriptor.equalsIgnoreClass("values", ArrayType.forType(thisClassType))) { // Enum[] values()
				return true;
			}
		}
		
		return false;
	}
	
	
	JavaMethod(ExtendedDataInputStream in, ClassInfo classinfo, ConstantPool pool) {
		this.modifiers = MethodModifiers.read(in);
		this.descriptor = new MethodDescriptor(classinfo.getThisType(), in, pool);
		
		this.attributes = Attributes.read(in, pool, Location.METHOD);
		this.codeAttribute = attributes.getOrDefault(AttributeType.CODE, EmptyCodeAttribute.INSTANCE);
		this.signature = attributes.getNullable(AttributeType.METHOD_SIGNATURE);
		
		if(signature != null)
			signature.checkTypes(descriptor, descriptor.getVisibleStartIndex(classinfo), attributes.getNullable(AttributeType.EXCEPTIONS));
		
		Logger.logf("Disassembling of method %s", descriptor);
		this.disassemblerContext = DisassemblerContext.disassemble(pool, codeAttribute.getCode());
		
		this.methodScope = MethodScope.of(classinfo, descriptor, modifiers, codeAttribute,
				disassemblerContext.getInstructions().size(), codeAttribute.isEmpty() ? descriptor.countLocals(modifiers) : codeAttribute.getMaxLocalsCount());
		
		this.stringifyContext = new StringifyContext(disassemblerContext, classinfo, this);
	}
	
	
	public static List<JavaMethod> readMethods(ExtendedDataInputStream in, ClassInfo classinfo, ConstantPool pool) {
		int length = in.readUnsignedShort();
		List<JavaMethod> methods = new ArrayList<>(length);
		
		for(int i = 0; i < length; i++) {
			methods.add(new JavaMethod(in, classinfo, pool));
		}
		
		return methods;
	}
	
	
	@Override
	public MethodModifiers getModifiers() {
		return modifiers;
	}
	
	public MethodDescriptor getDescriptor() {
		return descriptor;
	}
	
	public Attributes getAttributes() {
		return attributes;
	}
	
	public CodeAttribute getCodeAttribute() {
		return codeAttribute;
	}
	
	
	public StringifyContext getStringifyContext() {
		return stringifyContext;
	}
	
	
	public MethodScope getMethodScope() {
		return methodScope;
	}
	
	public MethodInfo getMethodInfo() {
		return methodInfo == null ? methodInfo = new MethodInfo(descriptor, modifiers) : methodInfo;
	}
	
	
	public void decompile(ClassInfo classinfo, ConstantPool pool) {
		Logger.logf("Decompiling of method %s", descriptor);
		
		try {
			decompilationContext = DecompilationContext.decompile(disassemblerContext, classinfo, this, disassemblerContext.getInstructions(), codeAttribute.getMaxLocalsCount());
		} catch(DecompilationException ex) {
			ex.printStackTrace();
			exceptionMessage = ex.getFullMessage();
		}
		
		if(JDecompiler.getConfig().useOverrideAnnotation() && descriptor.isPlain() && modifiers.isNotStatic()) {
			resolveOverrideAnnotation(classinfo);
		}
	}
	
	
	private void resolveOverrideAnnotation(ClassInfo classinfo) {
		
		if(classinfo.getThisType().equals(ClassType.OBJECT)) {
			return;
		}
		
		for(IClassInfo currentClassinfo = classinfo;;) {
			
			IClassInfo superClassinfo = ClassInfo.findClassInfo(currentClassinfo.getSuperType());
			
			if(superClassinfo == null) {
				break;
			}
			
			if(superClassinfo.hasMethodByDescriptor(descriptor::equalsIgnoreClass)) {
				hasOverrideAttribute = true;
				break;
			}
			
			currentClassinfo = superClassinfo;
		}
	}
	
	
	@Override
	public void addImports(ClassInfo classinfo) {
		attributes.addImports(classinfo);
		descriptor.addImports(classinfo);
		
		if(decompilationContext != null)
			decompilationContext.addImports(classinfo);
		
		if(hasOverrideAttribute)
			classinfo.addImport(ClassType.OVERRIDE);
	}
	
	
	@Override
	public boolean canStringify(ClassInfo classinfo) {
		return (!modifiers.isSyntheticOrBridge() ||
				modifiers.isSynthetic() && JDecompiler.getConfig().showSynthetic() ||
				modifiers.isBridge() && JDecompiler.getConfig().showBridge()) &&
				(JDecompiler.getConfig().showAutogenerated() || !isAutogenerated(classinfo));
	}
	
	
	public void writeAsLambda(StringifyOutputStream out, ClassInfo classinfo, int captured, List<Operation> capturedArguments) {
		
		if(codeAttribute.isEmpty()) {
			throw new DecompilationException("Cannot write method without Code attribute as lambda");
		}
		
		methodScope.assignVariablesNames();
		
		if(decompilationContext != null &&
				descriptor.argumentsEquals(PrimitiveType.INT) &&
				methodScope.writeAsLambdaNewArray(out, stringifyContext)) {
			
			return;
		}
		
		descriptor.writeAsLambda(out, stringifyContext, attributes, signature, captured);
		
		out.printsp().print("->");
		
		if(decompilationContext == null) {
			Type returnType = descriptor.getReturnType();
			
			out.printsp().print(
				returnType.isPrimitive() ?
						returnType == PrimitiveType.VOID ? "{}" :
						returnType == PrimitiveType.BOOLEAN ? "false" : "0" :
						"null");
			
			if(exceptionMessage != null) {
				out.print(" /* ").print(exceptionMessage).print(" */");
			}
			
		} else {
			for(int slot = 0; slot < capturedArguments.size(); slot++) {
				Operation argument = capturedArguments.get(slot);
				
				if(argument instanceof LoadOperation loadOperation)
					methodScope.getDefinedVariable(slot).setName(loadOperation.getVariable().getName());
			}
			
			out.printUsingFunction(methodScope, stringifyContext, MethodScope::writeAsLabmda);
		}
	}
	
	
	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		
		methodScope.assignVariablesNames();
		
		if(hasOverrideAttribute)
			out.printIndent().print('@').println(ClassType.OVERRIDE, classinfo);
		
		writeAnnotations(out, classinfo, attributes);
		
		out.printIndent().print(modifiersToString(classinfo), classinfo);
		descriptor.write(out, stringifyContext, attributes, signature);
		
		attributes.getOrDefault(AttributeType.EXCEPTIONS, ExceptionsAttribute.empty()).write(out, classinfo, signature);
		
		out.printIfNotNull(attributes.getNullable(AttributeType.ANNOTATION_DEFAULT), classinfo);
		
		if(codeAttribute.isEmpty()) {
			out.write(';');
			
		} else if(decompilationContext == null) {
			out.print(';').print(" /* ").print(exceptionMessage != null ? exceptionMessage : "null").print(" */");
			
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
		
		if(descriptor.isStaticInitializer()) {
			if(modifiers.getValue() == ACC_STATIC) {
				return WhitespaceStringBuilder.empty();
			} else {
				throw new IllegalModifiersException("static initializer must have only static modifier");
			}
		}
		
		IWhitespaceStringBuilder str = new WhitespaceStringBuilder().printTrailingSpace();
		
		var modifiers = this.modifiers;
		var classModifiers = classinfo.getModifiers();
		
		switch(modifiers.and(ACC_ACCESS_FLAGS)) {
			case ACC_VISIBLE -> {}
			
			case ACC_PUBLIC -> { // Все нестатические методы интерфейса по умолчанию имеют модификатор public, поэтому в этом случае нам не нужно выводить public
				if(JDecompiler.getConfig().printImplicitModifiers() || !classModifiers.isInterface())
					str.append("public");
			}
			
			case ACC_PRIVATE -> { // Конструкторы Enum по умолчанию имеют модификатор private, поэтому нам не нужно выводить private
				if(JDecompiler.getConfig().printImplicitModifiers() || !(classModifiers.isEnum() && descriptor.isConstructor() && descriptor.getDeclaringClass().equals(classinfo.getThisType())))
					str.append("private");
			}
			
			case ACC_PROTECTED -> {
				str.append("protected");
			}
			
			default -> {
				throw new IllegalModifiersException(this, modifiers, ILLEGAL_ACCESS_MODIFIERS_MESSAGE);
			}
		}
		
		if(modifiers.isStatic())
			str.append("static");
		
		if(modifiers.isAbstract()) {
			
			if(modifiers.isAny(ACC_STATIC | ACC_FINAL | ACC_SYNCHRONIZED | ACC_NATIVE | ACC_STRICT))
				throw new IllegalModifiersException(this, modifiers,
						"abstract method cannot be static, final, synchronized, native or strict");
			
			if(classModifiers.isNotInterface())
				str.append("abstract");
			
		} else {
			if(classModifiers.isInterface() && modifiers.isNotStatic() && modifiers.isNotPrivate())
				str.append("default");
		}
		
		if(modifiers.isFinal()) str.append("final");
		if(modifiers.isSynchronized()) str.append("synchronized");
		
		if(modifiers.isNative() && modifiers.isStrictfp())
			throw new IllegalModifiersException(this, modifiers, "method cannot be both native and strictfp");
		
		if(modifiers.isNative()) str.append("native");
		else if(modifiers.isStrictfp()) str.append("strictfp");
		
		return str;
	}
	
	@Override
	public String toString() {
		return modifiers + " " + descriptor;
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