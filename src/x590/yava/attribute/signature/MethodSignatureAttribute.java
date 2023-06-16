package x590.yava.attribute.signature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import x590.yava.attribute.ExceptionsAttribute;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedStringInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.method.MethodDescriptor;
import x590.yava.type.Type;
import x590.yava.type.reference.ReferenceType;
import x590.yava.type.reference.generic.GenericDeclarationType;
import x590.yava.type.reference.generic.GenericParameters;
import x590.util.CollectionUtil;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;

public final class MethodSignatureAttribute extends SignatureAttribute {
	
	private final GenericParameters<GenericDeclarationType> parameters;
	private final @Immutable List<Type> arguments;
	private final Type returnType;
	private final @Immutable List<ReferenceType> exceptionTypes;
	
	public MethodSignatureAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);
		
		ExtendedStringInputStream signatureIn = new ExtendedStringInputStream(pool.getUtf8String(in.readUnsignedShort()));
		
		this.parameters = Type.parseEmptyableGenericParameters(signatureIn);
		this.arguments = Type.parseMethodArguments(signatureIn);
		this.returnType = Type.parseReturnType(signatureIn);
		
		if(signatureIn.get() == '^') {
			
			List<ReferenceType> throwsTypes = new ArrayList<>();
			
			while(signatureIn.isAvailable() && signatureIn.get() == '^') {
				signatureIn.incPos();
				throwsTypes.add(Type.parseSignatureParameter(signatureIn));
			}
			
			this.exceptionTypes = Collections.unmodifiableList(throwsTypes);
			
		} else {
			this.exceptionTypes = Collections.emptyList();
		}
	}
	
	public GenericParameters<GenericDeclarationType> getParameters() {
		return parameters;
	}
	
	public @Immutable List<Type> getArguments() {
		return arguments;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public @Immutable List<ReferenceType> getExceptionTypes() {
		return exceptionTypes;
	}
	
	public boolean hasGenericTypes() {
		return !parameters.isEmpty() || arguments.stream().anyMatch(Type::isGenericType) ||
				returnType.isGenericType() || !exceptionTypes.isEmpty();
	}
	
	@Override
	public void addImports(ClassInfo classinfo) {
		parameters.addImports(classinfo);
		returnType.addImports(classinfo);
		classinfo.addImportsFor(arguments);
	}
	
	public void checkTypes(MethodDescriptor descriptor, int skip, @Nullable ExceptionsAttribute excepionsAttr) {
		
		List<Type> descriptorArguments = descriptor.getArguments();
		
		if(!CollectionUtil.collectionsEquals(arguments, descriptorArguments.subList(skip, descriptorArguments.size()), Type::equalsIgnoreSignature)) {
			throw new DecompilationException(
					"Method signature doesn't matches the arguments: (" + argumentsToString(arguments) + ")"
					+ " and (" + argumentsToString(descriptorArguments.subList(skip, descriptorArguments.size())) + ")"
			);
		}
		
		if(!returnType.equalsIgnoreSignature(descriptor.getReturnType())) {
			throw new DecompilationException("Method signature doesn't matches the return type: " + returnType + " and " + descriptor.getReturnType());
		}
		
		if(!exceptionTypes.isEmpty()) {
			if(excepionsAttr == null || !CollectionUtil.collectionsEquals(exceptionTypes, excepionsAttr.getExceptionTypes(), Type::equalsIgnoreSignature)) {
				throw new DecompilationException("Method signature doesn't matches the \"Excepions\" attribute: " + argumentsToString(exceptionTypes) +
						" and " + (excepionsAttr == null ? "<null>" : argumentsToString(excepionsAttr.getExceptionTypes())));
			}
		}
	}
	
	private static String argumentsToString(List<? extends Type> arguments) {
		return arguments.stream().map(Type::toString).collect(Collectors.joining(", "));
	}
	
	public MethodDescriptor createGenericDescriptor(ClassInfo classinfo, MethodDescriptor descriptor) {
		return MethodDescriptor.of(returnType.replaceUndefiniteGenericsToDefinite(classinfo, parameters), descriptor.getDeclaringClass(), descriptor.getName(),
				Stream.concat(
					descriptor.getArguments().stream().limit(descriptor.getArgumentsCount() - arguments.size()),
					arguments.stream().map(arg -> arg.replaceUndefiniteGenericsToDefinite(classinfo, parameters))
				).toList());
	}
	
	
	public void writeParametersIfNotEmpty(StringifyOutputStream out, ClassInfo classinfo) {
		var parameters = this.parameters;
		
		if(!parameters.isEmpty())
			out.printsp(parameters, classinfo);
	}
}
