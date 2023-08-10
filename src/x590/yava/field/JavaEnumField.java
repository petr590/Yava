package x590.yava.field;

import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.exception.decompilation.IllegalModifiersException;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.modifiers.FieldModifiers;
import x590.yava.operation.invoke.InvokeOperation;

import static x590.yava.method.MethodDescriptor.IMPLICIT_ENUM_ARGUMENTS;
import static x590.yava.modifiers.Modifiers.*;

public final class JavaEnumField extends JavaField {

	JavaEnumField(ExtendedDataInputStream in, ClassInfo classinfo, ConstantPool pool, FieldModifiers modifiers) {
		super(in, classinfo, pool, modifiers);

		if (classinfo.getModifiers().isNotEnum()) {
			throw new IllegalModifiersException("Cannot declare enum field in not enum class");
		}

		if (modifiers.notAllOf(ACC_PUBLIC | ACC_STATIC | ACC_FINAL)) {
			throw new IllegalModifiersException("Enum field must be public static and final, got " + modifiers);
		}
	}


	public void checkHasEnumInitializer() {
		if (!(getInitializer() instanceof InvokeOperation)) {
			throw new DecompilationException("Enum constant " + getDescriptor() + " must have enum initializer." +
					" Got: " + getInitializer());
		}
	}

	public InvokeOperation getEnumInitializer() {
		return (InvokeOperation) getInitializer();
	}

	public boolean hasArgumentsInEnumInitializer() {
		return getEnumInitializer().factualArgumentsCount() > IMPLICIT_ENUM_ARGUMENTS;
	}


	@Override
	public boolean canStringify(ClassInfo classinfo) {
		return false;
	}

	@Override
	public void writeNameAndInitializer(StringifyOutputStream out, ClassInfo classinfo) {

		var initializer = getEnumInitializer();
		var context = classinfo.getStaticInitializerStringifyContext();

		out.write(getDescriptor().getName());

		if (initializer.factualArgumentsCount() > IMPLICIT_ENUM_ARGUMENTS) {
			out.print('(').printAll(initializer.getFactualArguments(), IMPLICIT_ENUM_ARGUMENTS, context, ", ").print(')');
		}
	}

	public void writeIndent(StringifyOutputStream out, JavaEnumField nextField) {
		if (hasArgumentsInEnumInitializer() || nextField.hasArgumentsInEnumInitializer())
			out.println(',').printIndent();
		else
			out.print(", ");
	}
}
