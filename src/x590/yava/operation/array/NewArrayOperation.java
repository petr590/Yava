package x590.yava.operation.array;

import x590.util.annotation.Immutable;
import x590.yava.clazz.ClassInfo;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.AConstNullOperation;
import x590.yava.operation.constant.IConstOperation;
import x590.yava.operation.constant.ZeroConstOperation;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ArrayType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewArrayOperation extends AbstractOperation {

	private final ArrayType arrayType;
	private final @Immutable List<Operation> arrayLengths;
	private final int length;
	private final List<Operation> initializers = new ArrayList<>();
	private final @Immutable List<Operation> immutableInitializers = Collections.unmodifiableList(initializers);
	private boolean shortArrayInitializerAllowed, varargsInlined;

	public NewArrayOperation(DecompilationContext context, int index, int dimensions) {
		this(context, context.pool.getClassConstant(index).toArrayType(), dimensions);
	}

	public NewArrayOperation(DecompilationContext context, ArrayType arrayType) {
		this(context, arrayType, 1);
	}

	public NewArrayOperation(DecompilationContext context, ArrayType arrayType, int dimensions) {

		if (dimensions > arrayType.getNestingLevel())
			throw new DecompilationException("Instruction newarray (or another derivative of it)" +
					"has too many dimensions (" + dimensions + ") for its array type " + arrayType);

		this.arrayType = arrayType;

		List<Operation> arrayLengths = new ArrayList<>(dimensions);

		for (int i = dimensions; i > 0; i--)
			arrayLengths.add(context.popAsNarrowest(PrimitiveType.INT));

		Collections.reverse(arrayLengths);

		this.arrayLengths = Collections.unmodifiableList(arrayLengths);
		this.length = arrayLengths.get(0) instanceof IConstOperation iconst ? iconst.getValue() : -1;
	}


	@Override
	public Type getReturnType() {
		return arrayType;
	}

	public @Immutable List<Operation> getLengths() {
		return arrayLengths;
	}

	public int getLength() {
		return length;
	}

	public @Immutable List<Operation> getInitializers() {
		return immutableInitializers;
	}

	private void fillInitializersWithZeros(int toIndex) {
		for (int i = initializers.size(); i < toIndex; i++) {
			initializers.add(arrayType.getElementType().isPrimitive() ? ZeroConstOperation.INSTANCE : AConstNullOperation.INSTANCE);
		}
	}

	public boolean addToInitializer(Operation operation, IConstOperation indexOperation) {
		int index = indexOperation.getValue();

		if (index >= 0 && (length == -1 || index < length)) {
			fillInitializersWithZeros(index);
			operation.allowImplicitCast();
			initializers.add(operation);
			return true;
		}

		return false;
	}

	/**
	 * Убирает явное объявление массива при вызове метода с varargs
	 */
	@Deprecated
	public void inlineVarargs() {
		if (canInitAsList()) {
			varargsInlined = true;
			initializers.forEach(Operation::denyImplicitCast);
		}
	}


	@Override
	public void allowShortArrayInitializer() {
		this.shortArrayInitializerAllowed = true;
	}


	public boolean canInitAsList() {
		return !initializers.isEmpty() || arrayLengths.size() == 1 && length == 0;
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		if (!varargsInlined) {
			arrayType.addImports(classinfo);
			classinfo.addImportsFor(arrayLengths);
		}

		classinfo.addImportsFor(initializers);
	}


	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if (varargsInlined) {
			out.printAll(initializers, context, ", ");
			return;
		}

		if (!initializers.isEmpty() && length != -1)
			fillInitializersWithZeros(length);

		if (canInitAsList() && (shortArrayInitializerAllowed || !initializers.isEmpty())) {

			if (!shortArrayInitializerAllowed) {
				out.printsp("new").printsp(arrayType, context.getClassinfo());
			}

			if (initializers.isEmpty()) {
				out.write("{}");

			} else {
				initializers.forEach(Operation::allowShortArrayInitializer);

				out.print(canUseSpaceFor(initializers.get(0)) ? "{ " : "{")
						.printAll(initializers, context, ", ")
						.print(canUseSpaceFor(initializers.get(initializers.size() - 1)) ? " }" : "}");
			}

		} else {
			out.printsp("new").print(arrayType.getMemberType(), context.getClassinfo())
					.printEachUsingFunction(arrayLengths, arrayLength -> out.print('[').print(arrayLength, context).print(']'));

			for (int i = arrayLengths.size(), nestLevel = arrayType.getNestingLevel(); i < nestLevel; i++)
				out.write("[]");
		}
	}


	private boolean canUseSpaceFor(Operation operation) {
		return !(operation instanceof NewArrayOperation newArray) || !newArray.canInitAsList();
	}

	@Override
	public boolean requiresLocalContext() {
		return initializers.stream().anyMatch(Operation::requiresLocalContext) ||
				arrayLengths.stream().anyMatch(Operation::requiresLocalContext);
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof NewArrayOperation operation &&
				arrayType.equals(operation.arrayType) && arrayLengths.equals(operation.arrayLengths) &&
				initializers.equals(operation.initializers) && varargsInlined == operation.varargsInlined;
	}
}
