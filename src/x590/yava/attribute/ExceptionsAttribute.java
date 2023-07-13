package x590.yava.attribute;

import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.attribute.signature.MethodSignatureAttribute;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.disassembling.DisassemblingException;
import x590.yava.io.*;
import x590.yava.type.reference.RealReferenceType;
import x590.yava.type.reference.ReferenceType;

import java.util.Collections;
import java.util.List;

public class ExceptionsAttribute extends Attribute {

	private final @Immutable List<? extends RealReferenceType> exceptionTypes;

	ExceptionsAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);

		int exceptionsLength = in.readUnsignedShort();

		if (exceptionsLength == 0)
			throw new DisassemblingException("The \"" + AttributeNames.EXCEPTIONS + "\" attribute cannot be empty");

		this.exceptionTypes = in.readImmutableList(exceptionsLength, () -> pool.getClassConstant(in.readUnsignedShort()).toClassType());
	}

	ExceptionsAttribute(String name, AssemblingInputStream in, ConstantPool pool) {
		super(name);

		in.requireNext('{');

		this.exceptionTypes = in.nextClassTypesStream().toList();

		in.requireNext('}');

		initLength(Sizes.LENGTH + Sizes.CONSTPOOL_INDEX * exceptionTypes.size());
	}

	private ExceptionsAttribute(String name, int length, @Immutable List<? extends RealReferenceType> exceptionTypes) {
		super(name, length);
		this.exceptionTypes = exceptionTypes;
	}

	public @Immutable List<? extends ReferenceType> getExceptionTypes() {
		return exceptionTypes;
	}

	public static ExceptionsAttribute empty() {
		return EmptyExceptionsAttribute.INSTANCE;
	}


	private static class EmptyExceptionsAttribute extends ExceptionsAttribute {

		private static final ExceptionsAttribute INSTANCE = new EmptyExceptionsAttribute();

		private EmptyExceptionsAttribute() {
			super(AttributeNames.EXCEPTIONS, 0, Collections.emptyList());
		}

		@Override
		public void addImports(ClassInfo classinfo) {}

		@Override
		public void write(StringifyOutputStream out, ClassInfo classinfo, @Nullable MethodSignatureAttribute signature) {}
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImportsFor(exceptionTypes);
	}

	public void write(StringifyOutputStream out, ClassInfo classinfo, @Nullable MethodSignatureAttribute signature) {
		List<? extends ReferenceType> exceptions;

		if (signature != null) {
			var signatureExceptionTypes = signature.getExceptionTypes();
			exceptions = signatureExceptionTypes.isEmpty() ? this.exceptionTypes : signatureExceptionTypes;
		} else {
			exceptions = this.exceptionTypes;
		}

		out.print(" throws ").printAll(exceptions, classinfo, ", ");
	}

	@Override
	protected void writeDisassembledContent(DisassemblingOutputStream out, ClassInfo classinfo) {
		out.printIndent().printAll(exceptionTypes, classinfo, ", ").println();
	}

	@Override
	protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
		out.recordAllAsShorts(exceptionTypes.size(), exceptionTypes.stream().mapToInt(pool::classIndexFor));
	}
}
