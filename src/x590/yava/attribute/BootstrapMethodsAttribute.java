package x590.yava.attribute;

import x590.util.annotation.Immutable;
import x590.yava.constpool.constvalue.ConstValueConstant;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.MethodHandleConstant;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.io.ExtendedDataInputStream;

import java.util.List;

public final class BootstrapMethodsAttribute extends Attribute {

	private final @Immutable List<BootstrapMethod> bootstrapMethods;

	public BootstrapMethodsAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);
		this.bootstrapMethods = in.readImmutableList(() -> new BootstrapMethod(in, pool));
	}

	public BootstrapMethod getBootstrapMethod(int index) {
		return bootstrapMethods.get(index);
	}


	public static class BootstrapMethod {

		private final MethodHandleConstant methodHandle;
		private final @Immutable List<ConstValueConstant> arguments;

		public BootstrapMethod(ExtendedDataInputStream in, ConstantPool pool) {
			this.methodHandle = pool.get(in.readUnsignedShort());
			this.arguments = in.readImmutableList(() -> pool.get(in.readUnsignedShort()));
		}

		public MethodHandleConstant getMethodHandle() {
			return methodHandle;
		}

		public @Immutable List<ConstValueConstant> getArguments() {
			return arguments;
		}

		@SuppressWarnings("unchecked")
		public <C extends ConstValueConstant> C getArgument(int index) {
			return (C) arguments.get(index);
		}

		public <C extends ConstValueConstant> C getArgument(Class<C> clazz, int index) {
			C constant = getArgument(index);

			if (clazz.isInstance(constant))
				return constant;

			throw new DecompilationException("Method java.lang.invoke.StringConcatFactory.makeConcatWithConstants"
					+ ": wrong type of static argument #" + index
					+ ": expected String, got " + constant.getConstantName());
		}

		public int getArgumentsCount() {
			return arguments.size();
		}
	}
}
