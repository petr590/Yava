package x590.yava.field;

import static x590.yava.modifiers.Modifiers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import x590.yava.JavaClassElement;
import x590.yava.attribute.AttributeType;
import x590.yava.attribute.Attributes;
import x590.yava.attribute.Attributes.Location;
import x590.yava.attribute.ConstantValueAttribute;
import x590.yava.attribute.annotation.AnnotationsAttribute;
import x590.yava.attribute.signature.FieldSignatureAttribute;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstValueConstant;
import x590.yava.constpool.ConstantPool;
import x590.yava.context.DecompilationContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.exception.decompilation.IllegalModifiersException;
import x590.yava.io.*;
import x590.yava.main.Yava;
import x590.yava.method.JavaMethod;
import x590.yava.modifiers.FieldModifiers;
import x590.yava.operation.Operation;
import x590.yava.util.IWhitespaceStringBuilder;
import x590.yava.util.WhitespaceStringBuilder;
import x590.util.Pair;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;

public class JavaField extends JavaClassElement {
	
	private final FieldModifiers modifiers;
	private final FieldDescriptor descriptor;
	private final FieldDescriptor genericDescriptor;
	private final Attributes attributes;
	
	private @Nullable FieldInfo fieldInfo;
	
	private final @Nullable ConstantValueAttribute constantValueAttribute;
	private Operation initializer;
	private JavaMethod method;
	
	private final Pair<AnnotationsAttribute, AnnotationsAttribute> annotationAttributes;
	
	private final boolean isRecordComponent;
	
	protected JavaField(ExtendedDataInputStream in, ClassInfo classinfo, ConstantPool pool, FieldModifiers modifiers) {
		this.modifiers = modifiers;
		this.descriptor = FieldDescriptor.from(classinfo.getThisType(), in, pool);
		this.attributes = Attributes.read(in, pool, Location.FIELD);
		
		var signature = attributes.getNullable(AttributeType.FIELD_SIGNATURE);
		
		if(signature != null) {
			signature.checkType(descriptor);
			this.genericDescriptor = signature.createGenericDescriptor(classinfo, descriptor);
		} else {
			this.genericDescriptor = descriptor;
		}
		
		this.constantValueAttribute = attributes.getNullable(AttributeType.CONSTANT_VALUE);
		this.annotationAttributes = new Pair<>(attributes.getNullable(AttributeType.RUNTIME_VISIBLE_ANNOTATIONS), attributes.getNullable(AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS));
		this.isRecordComponent = classinfo.isRecord() && modifiers.isNotStatic();
	}

	protected JavaField(AssemblingInputStream in, ClassInfo classinfo, ConstantPool pool) {
		this.modifiers = FieldModifiers.parse(in);
		this.descriptor = FieldDescriptor.from(classinfo.getThisType(), in);

		this.attributes = Attributes.parse(in, pool, Location.FIELD);

		this.genericDescriptor = descriptor;
		this.constantValueAttribute = null;
		this.annotationAttributes = Pair.empty();
		this.isRecordComponent = false;
	}
	
	
	public static List<JavaField> readFields(ExtendedDataInputStream in, ClassInfo classinfo, ConstantPool pool) {
		return in.readArrayList(() -> {
			FieldModifiers modifiers = FieldModifiers.read(in);
			return modifiers.isEnum() ? new JavaEnumField(in, classinfo, pool, modifiers) : new JavaField(in, classinfo, pool, modifiers);
		});
	}

	public static @Immutable List<JavaField> parseFields(AssemblingInputStream in, ClassInfo classinfo, ConstantPool pool) {
		if(in.advanceIfHasNext("fields")) {
			in.requireNext('{');

			List<JavaField> fields = new ArrayList<>();

			while(!in.advanceIfHasNext("}")) {
				fields.add(new JavaField(in, classinfo, pool));
			}

			return Collections.unmodifiableList(fields);
		}

		return Collections.emptyList();
	}


	public boolean isRecordComponent() {
		return isRecordComponent;
	}
	
	
	public boolean canStringifyAsRecordComponent(ClassInfo classinfo) {
		return isRecordComponent && super.canStringify(classinfo);
	}
	
	@Override
	public boolean canStringify(ClassInfo classinfo) {
		return !isRecordComponent && super.canStringify(classinfo);
	}
	
	
	@Override
	public FieldModifiers getModifiers() {
		return modifiers;
	}
	
	public FieldDescriptor getDescriptor() {
		return descriptor;
	}
	
	public Attributes getAttributes() {
		return attributes;
	}
	
	public FieldInfo getFieldInfo() {
		return fieldInfo == null ? fieldInfo = new FieldInfo(descriptor, genericDescriptor, modifiers) : fieldInfo;
	}
	
	
	public boolean setStaticInitializer(Operation initializer, DecompilationContext context) {
		
		if(modifiers.isNotStatic()) {
			throw new DecompilationException("Cannot set static initializer to the non-static field \"" + descriptor.getName() + "\"");
		}
		
		if(this.initializer == null && constantValueAttribute == null && !isRecordComponent) {
			this.initializer = initializer;
			this.method = context.getMethod();
			return true;
		}
		
		return false;
	}
	
	
	public boolean setInstanceInitializer(Operation initializer, DecompilationContext context) {
		
		if(modifiers.isStatic()) {
			throw new DecompilationException("Cannot set instance initializer to static field \"" + descriptor.getName() + "\"");
		}
		
		if(constantValueAttribute != null || isRecordComponent) {
			return false;
		}
		
		if(this.initializer == null) {
			this.initializer = initializer;
			this.method = context.getMethod();
			return true;
		}
		
		return this.initializer.equals(initializer);
	}
	
	
	/** @throws IllegalArgumentException если поле не содержит атрибута ConstantValue */
	public ConstValueConstant getConstantValue() {
		if(constantValueAttribute != null)
			return constantValueAttribute.value;
		
		throw new IllegalArgumentException("Field does not contains ConstantValueAttribute");
	}
	
	
	/** @throws IllegalArgumentException если поле не содержит атрибута ConstantValue
	 * @throws IllegalArgumentException если поле содержит атрибут ConstantValue не того типа */
	public <C extends ConstValueConstant> C getConstantValueAs(Class<C> constantClass) {
		@SuppressWarnings("unchecked")
		C constant = (C)getConstantValue();
		
		if(constantClass.isInstance(constant))
			return constant;
		
		throw new IllegalArgumentException("Field does not contains ConstantValueAttribute of type " + constantClass.getName());
	}
	
	
	public @Nullable Operation getInitializer() {
		return initializer;
	}
	
	public boolean hasNoInitializers() {
		return constantValueAttribute == null && initializer == null;
	}
	
	
	public boolean isConstant() {
		return constantValueAttribute != null;
	}
	
	
	@Override
	public void addImports(ClassInfo classinfo) {
		attributes.addImports(classinfo);
		descriptor.addImports(classinfo);
	}
	
	
	public boolean hasAnnotation() {
		return attributes.has(AttributeType.RUNTIME_VISIBLE_ANNOTATIONS) || attributes.has(AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS);
	}
	
	public Pair<AnnotationsAttribute, AnnotationsAttribute> getAnnotationAttributes() {
		return annotationAttributes;
	}
	
	public FieldSignatureAttribute getSignature() {
		return attributes.getNullable(AttributeType.FIELD_SIGNATURE);
	}
	
	
	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		writeWithoutSemicolon(out, classinfo);
		out.println(';');
	}
	
	public void writeWithoutSemicolon(StringifyOutputStream out, ClassInfo classinfo) {
		writeAnnotations(out, classinfo, attributes);
		
		out.printIndent().print(isRecordComponent ? recordComponentModifiersToString() : modifiersToString(classinfo), classinfo);
		genericDescriptor.writeType(out, classinfo);
		
		writeNameAndInitializer(out.printsp(), classinfo);
	}
	
	public void writeNameAndInitializer(StringifyOutputStream out, ClassInfo classinfo) {
		
		out.write(descriptor.getName());
		descriptor.getType().writeRightDefinition(out, classinfo);
		
		if(initializer != null) {
			out.write(" = ");
			
			initializer.allowShortArrayInitializer();
			initializer.writeTo(out, method.getStringifyContext());
			
		} else if(constantValueAttribute != null) {
			out.write(" = ");
			constantValueAttribute.writeTo(out, classinfo, descriptor.getType(), descriptor);
		}
	}
	
	@Override
	public String getModifiersTarget() {
		return "field " + descriptor.toString();
	}
	
	private IWhitespaceStringBuilder modifiersToString(ClassInfo classinfo) {
		IWhitespaceStringBuilder str = new WhitespaceStringBuilder().printTrailingSpace();
		
		var modifiers = this.modifiers;
		
		boolean printImplicitInterfaceModifiers = Yava.getConfig().printImplicitModifiers() || !classinfo.getModifiers().isInterface();
		
		switch(modifiers.and(ACC_ACCESS_FLAGS)) {
			case ACC_VISIBLE   -> {}
			case ACC_PRIVATE   -> str.append("private");
			case ACC_PROTECTED -> str.append("protected");
			case ACC_PUBLIC    -> {
				if(printImplicitInterfaceModifiers)
					str.append("public");
			}
			
			default ->
				throw new IllegalModifiersException(this, modifiers, ILLEGAL_ACCESS_MODIFIERS_MESSAGE);
		}
		
		if(classinfo.getModifiers().isInterface() &&
				(modifiers.isNotPublic() || modifiers.isNotStatic() || modifiers.isNotFinal())) {
			
			throw new IllegalModifiersException(this, modifiers, "interface field must be public static final");
		}
		
		if(modifiers.isStatic() && printImplicitInterfaceModifiers) str.append("static");
		if(modifiers.isSynthetic()) str.append("/* synthetic */");
		
		if(modifiers.isFinal()) {
			if(modifiers.isVolatile())
				throw new IllegalModifiersException(this, modifiers, "field cannot be both final and volatile");
			
			if(printImplicitInterfaceModifiers)
				str.append("final");
		}
		
		if(modifiers.isTransient()) str.append("transient");
		if(modifiers.isVolatile())  str.append("volatile");
		
		return str;
	}
	
	private IWhitespaceStringBuilder recordComponentModifiersToString() {
		IWhitespaceStringBuilder str = new WhitespaceStringBuilder().printTrailingSpace();
		
		var modifiers = this.modifiers;
		
		if(modifiers.and(ACC_ACCESS_FLAGS) != ACC_PRIVATE)
			throw new IllegalModifiersException(this, modifiers, ILLEGAL_ACCESS_MODIFIERS_MESSAGE);
		
		if(modifiers.isStatic())
			throw new IllegalModifiersException(this, modifiers, "record component cannot be static");
		
		if(modifiers.isSynthetic()) str.append("/* synthetic */");
		
		if(modifiers.isVolatile())
			throw new IllegalModifiersException(this, modifiers, "record component cannot be volatile");
		
		if(modifiers.isTransient())
			throw new IllegalModifiersException(this, modifiers, "record component cannot be transient");
		
		return str;
	}
	
	@Override
	public String toString() {
		return modifiers.toSimpleString() + " " + descriptor;
	}
	
	
	public boolean canJoinDeclaration(JavaField other) {
		return hasNoInitializers() && other.hasNoInitializers() &&
				modifiers.equals(other.modifiers) &&
				descriptor.getType().getArrayMemberIfUsingCArrays()
						.equals(other.descriptor.getType().getArrayMemberIfUsingCArrays()) &&
				Objects.equals(getSignature(), other.getSignature()) &&
				annotationAttributes.equals(other.annotationAttributes);
	}
	
	
	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		out .print(modifiersToString(classinfo), classinfo)
			.print(descriptor, classinfo);
	}
	
	
	@Override
	public void serialize(ExtendedDataOutputStream out) {
		// TODO
	}
}
