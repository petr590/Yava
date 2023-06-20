package x590.yava.operation.invoke;

import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.method.MethodDescriptor;
import x590.yava.operation.Operation;

public abstract class InvokeNonstaticOperation extends InvokeOperation {

	protected final Operation object;

	public InvokeNonstaticOperation(DecompilationContext context, int index) {
		this(context, getDescriptor(context, index));
	}

	/**
	 * Не переделывать через делегирование конструктору this,
	 * так как важен порядок: сначала со стека снимаются аргументы, затем объект
	 */
	public InvokeNonstaticOperation(DecompilationContext context, MethodDescriptor descriptor) {
		super(context, descriptor);
		this.object = context.popAsNarrowest(descriptor.getDeclaringClass()).castIfNecessary(descriptor.getDeclaringClass());
		super.initGenericDescriptor(object);
	}

	/**
	 * Только для случаев, когда у метода нет аргументов или когда объект не на стеке
	 */
	public InvokeNonstaticOperation(DecompilationContext context, MethodDescriptor descriptor, Operation object) {
		super(context, descriptor);
		this.object = object = object.castIfNecessary(descriptor.getDeclaringClass());
		super.initGenericDescriptor(object);
	}


	@Override
	public Operation getObject() {
		return object;
	}


	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if (tryWriteObject(out, context)) {
			out.write('.');
		}

		out.print(getDescriptor().getName()).printUsingFunction(this, context, InvokeOperation::writeArguments);
	}

	/**
	 * Возвращает {@code true}, если объект записан
	 */
	protected boolean tryWriteObject(StringifyOutputStream out, StringifyContext context) {
		if (!canOmitObject(context, object)) {
			out.printPrioritied(this, object, context, Associativity.LEFT);
			return true;
		}

		return false;
	}

	@Override
	public boolean requiresLocalContext() {
		return object.requiresLocalContext() || super.requiresLocalContext();
	}
}
