package x590.yava.attribute.signature;

import x590.util.CollectionUtil;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.Keywords;
import x590.yava.attribute.AttributeNames;
import x590.yava.attribute.ExceptionsAttribute;
import x590.yava.attribute.Sizes;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.io.*;
import x590.yava.method.MethodDescriptor;
import x590.yava.type.Type;
import x590.yava.type.reference.ReferenceType;
import x590.yava.type.reference.generic.GenericDeclarationType;
import x590.yava.type.reference.generic.GenericParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MethodSignatureAttribute extends SignatureAttribute {

	private final GenericParameters<GenericDeclarationType> parameters;
	private final @Immutable List<Type> arguments;
	private final Type returnType;
	private final @Immutable List<? extends ReferenceType> exceptionTypes;

	public MethodSignatureAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);

		ExtendedStringInputStream signatureIn = new ExtendedStringInputStream(pool.getUtf8String(in.readUnsignedShort()));

		this.parameters = Type.parseEmptyableGenericParameters(signatureIn);
		this.arguments = Type.parseMethodArguments(signatureIn);
		this.returnType = Type.parseReturnType(signatureIn);

		if (signatureIn.get() == '^') {

			List<ReferenceType> throwsTypes = new ArrayList<>();

			while (signatureIn.isAvailable() && signatureIn.get() == '^') {
				signatureIn.incPos();
				throwsTypes.add(Type.parseSignatureParameter(signatureIn));
			}

			this.exceptionTypes = Collections.unmodifiableList(throwsTypes);

		} else {
			this.exceptionTypes = Collections.emptyList();
		}
	}

	public MethodSignatureAttribute(String name, AssemblingInputStream in, ConstantPool pool) {
		super(name, Sizes.CONSTPOOL_INDEX);

		if (in.requireNext('{').advanceIfHasNext('<')) {
			this.parameters = GenericParameters.of(in.nextGenericDeclarationTypesStream().toList());
			in.requireNext('>');
		} else {
			this.parameters = GenericParameters.empty();
		}

		this.returnType = in.nextParametrizedType();

		this.arguments = in.nextParametrizedMethodArguments().toList();

		if (in.advanceIfHasNext(Keywords.THROWS)) {
			this.exceptionTypes = in.nextParametrizedReferenceTypesStream().toList();
		} else {
			this.exceptionTypes = Collections.emptyList();
		}

		in.requireNext(';').requireNext('}');
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

	public @Immutable List<? extends ReferenceType> getExceptionTypes() {
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

	public void checkTypes(MethodDescriptor descriptor, int skip, @Nullable ExceptionsAttribute exceptionsAttr) {

		List<Type> descriptorArguments = descriptor.getArguments();

		if (!CollectionUtil.collectionsEquals(arguments, descriptorArguments.subList(skip, descriptorArguments.size()), Type::equalsIgnoreSignature)) {
			throw new DecompilationException(
					"Method signature doesn't matches the arguments: (" + argumentsToString(arguments) + ")"
							+ " and (" + argumentsToString(descriptorArguments.subList(skip, descriptorArguments.size())) + ")"
			);
		}

		if (!returnType.equalsIgnoreSignature(descriptor.getReturnType())) {
			throw new DecompilationException("Method signature doesn't matches the return type: " + returnType + " and " + descriptor.getReturnType());
		}

		if (!exceptionTypes.isEmpty()) {
			if (exceptionsAttr == null || !CollectionUtil.collectionsEquals(exceptionTypes, exceptionsAttr.getExceptionTypes(), Type::equalsIgnoreSignature)) {
				throw new DecompilationException("Method signature doesn't matches the \"" + AttributeNames.EXCEPTIONS + "\" attribute: " + argumentsToString(exceptionTypes) +
						" and " + (exceptionsAttr == null ? "<null>" : argumentsToString(exceptionsAttr.getExceptionTypes())));
			}
		}
	}

	private static String argumentsToString(List<? extends Type> arguments) {
		return arguments.stream().map(Type::toString).collect(Collectors.joining(", "));
	}

	public MethodDescriptor createGenericDescriptor(ClassInfo classinfo, MethodDescriptor descriptor) {
		return MethodDescriptor.of(
				returnType.replaceIndefiniteGenericsToDefinite(classinfo, parameters),
				descriptor.getDeclaringClass(), descriptor.getName(),
				Stream.concat(
						descriptor.getArguments().stream().limit(descriptor.getArgumentsCount() - arguments.size()),
						arguments.stream().map(arg -> arg.replaceIndefiniteGenericsToDefinite(classinfo, parameters))
				).toList()
		);
	}


	public void writeParametersIfNotEmpty(StringifyOutputStream out, ClassInfo classinfo) {
		var parameters = this.parameters;

		if (!parameters.isEmpty())
			out.printsp(parameters, classinfo);
	}

	@Override
	protected void writeDisassembledContent(DisassemblingOutputStream out, ClassInfo classinfo) {
		out.printIndent();

		if (!parameters.isEmpty()) {
			out.print('<').printAll(parameters.getTypes(), classinfo, ", ").printsp('>');
		}

		out .printsp(returnType, classinfo)
			.print('(').printAll(arguments, classinfo, ", ").print(')');

		if (!exceptionTypes.isEmpty()) {
			out.printsp().printsp(Keywords.THROWS).printAll(exceptionTypes, classinfo, ", ");
		}

		out.println(';');
	}
}
