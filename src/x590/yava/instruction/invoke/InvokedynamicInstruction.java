package x590.yava.instruction.invoke;

import x590.util.IntegerUtil;
import x590.yava.attribute.BootstrapMethodsAttribute.BootstrapMethod;
import x590.yava.constpool.InvokeDynamicConstant;
import x590.yava.constpool.MethodHandleConstant;
import x590.yava.constpool.MethodHandleConstant.ReferenceKind;
import x590.yava.constpool.MethodrefConstant;
import x590.yava.constpool.constvalue.StringConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.DisassemblerContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.method.MethodDescriptor;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.StringConstOperation;
import x590.yava.operation.field.GetInstanceFieldOperation;
import x590.yava.operation.field.GetStaticFieldOperation;
import x590.yava.operation.field.PutInstanceFieldOperation;
import x590.yava.operation.field.PutStaticFieldOperation;
import x590.yava.operation.invoke.ConcatStringsOperation;
import x590.yava.operation.invoke.LambdaOperation;
import x590.yava.operation.invoke.RecordSyntheticOperation;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ArrayType;
import x590.yava.type.reference.ClassType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static x590.yava.type.reference.ArrayType.OBJECT_ARRAY;
import static x590.yava.type.reference.ClassType.*;

public final class InvokedynamicInstruction extends InvokeInstruction {

	public InvokedynamicInstruction(DisassemblerContext context, int index, int zeroShort) {
		super(index);

		if (zeroShort != 0) {
			context.warning("illegal format of instruction invokedynamic at pos 0x" + IntegerUtil.hex(context.currentPos()) +
					": by specification, third and fourth bytes must be zero");
		}
	}


	private static final String MAKE_CONCAT_WITH_CONSTANTS = "makeConcatWithConstants";

	private static final ClassType
			CALL_SITE = ClassType.fromDescriptor("java/lang/invoke/CallSite"),
			LOOKUP = ClassType.fromDescriptor("java/lang/invoke/MethodHandles$Lookup"),
			STRING_CONCAT_FACTORY = ClassType.fromDescriptor("java/lang/invoke/StringConcatFactory"),
			TYPE_DESCRIPTOR = ClassType.fromDescriptor("java/lang/invoke/TypeDescriptor"),
			LAMBDA_METAFACTORY = ClassType.fromDescriptor("java/lang/invoke/LambdaMetafactory"),
			OBJECT_METHODS = ClassType.fromDescriptor("java/lang/runtime/ObjectMethods");

	private static final ArrayType
			METHOD_HANDLE_ARRAY = METHOD_HANDLE.arrayType();

	private static final MethodDescriptor
			MAKE_CONCAT_WITH_CONSTANTS_DESCRIPTOR = MethodDescriptor.of(CALL_SITE, STRING_CONCAT_FACTORY, MAKE_CONCAT_WITH_CONSTANTS, LOOKUP, STRING, METHOD_TYPE, STRING, OBJECT_ARRAY),
			LAMBDA_METAFACTORY_DESCRIPTOR = MethodDescriptor.of(CALL_SITE, LAMBDA_METAFACTORY, "metafactory", LOOKUP, STRING, METHOD_TYPE, METHOD_TYPE, METHOD_HANDLE, METHOD_TYPE),
			OBJECT_METHODS_BOOTSTRAP_DESCRIPTOR = MethodDescriptor.of(OBJECT, OBJECT_METHODS, "bootstrap", LOOKUP, STRING, TYPE_DESCRIPTOR, CLASS, STRING, METHOD_HANDLE_ARRAY);

//	private static final MethodDescriptor publicLookupDescriptor = new MethodDescriptor(LOOKUP, "publicLookup", LOOKUP);


	@Override
	public Operation toOperation(DecompilationContext context) {
		InvokeDynamicConstant invokeDynamicConstant = context.pool.get(index);
		BootstrapMethod bootstrapMethod = invokeDynamicConstant.getBootstrapMethod(context.getClassinfo().getAttributes());

		MethodHandleConstant methodHandle = bootstrapMethod.getMethodHandle();

		switch (methodHandle.getReferenceKind()) {
			case GETFIELD:
				return new GetInstanceFieldOperation(context, methodHandle.getFieldrefConstant());
			case GETSTATIC:
				return new GetStaticFieldOperation(context, methodHandle.getFieldrefConstant());
			case PUTFIELD:
				return new PutInstanceFieldOperation(context, methodHandle.getFieldrefConstant());
			case PUTSTATIC:
				return new PutStaticFieldOperation(context, methodHandle.getFieldrefConstant());

			default: {
				MethodrefConstant methodrefConstant = methodHandle.getMethodrefConstant();

				MethodDescriptor lambdaDescriptor =
						MethodDescriptor.from(methodrefConstant.getClassConstant(), invokeDynamicConstant.getNameAndType());

				List<Operation> arguments = new ArrayList<>(lambdaDescriptor.getArgumentsCount());

				// pop arguments that already on stack
				for (int i = lambdaDescriptor.getArgumentsCount(); i > 0; i--)
					arguments.add(context.pop());

				Collections.reverse(arguments);


				MethodDescriptor invokedynamicDescriptor = methodrefConstant.toDescriptor();

				if (methodHandle.getReferenceKind() == ReferenceKind.INVOKESTATIC) {

					if (lambdaDescriptor.getName().equals(MAKE_CONCAT_WITH_CONSTANTS) && invokedynamicDescriptor.equals(MAKE_CONCAT_WITH_CONSTANTS_DESCRIPTOR)) {

						// String concat
						if (bootstrapMethod.getArgumentsCount() < 1) {
							throw new DecompilationException("Method java.lang.invoke.StringConcatFactory.makeConcatWithConstants" +
									" must have one or more static arguments");
						}

						StringConstOperation pattern = new StringConstOperation(bootstrapMethod.getArgument(StringConstant.class, 0));


						int argumentsCount = bootstrapMethod.getArgumentsCount();
						List<Operation> staticArguments = new ArrayList<>(argumentsCount - 1);

						for (int i = 1; i < argumentsCount; i++) {
							staticArguments.add(new StringConstOperation(bootstrapMethod.getArgument(StringConstant.class, i)));
						}

						// push non-static arguments on stack
						context.pushAll(arguments);

						return new ConcatStringsOperation(context, lambdaDescriptor, pattern, staticArguments);
					}


					if (invokedynamicDescriptor.equals(LAMBDA_METAFACTORY_DESCRIPTOR)) {

						if (bootstrapMethod.getArgumentsCount() != 3) {
							throw new DecompilationException("Method java.lang.invoke.LambdaMetafactory.metafactory" +
									" must have three static arguments");
						}

						return new LambdaOperation(context, lambdaDescriptor, arguments, bootstrapMethod.getArgument(1));
					}


					if (invokedynamicDescriptor.equals(OBJECT_METHODS_BOOTSTRAP_DESCRIPTOR) &&
							lambdaDescriptor.getDeclaringClass().equals(OBJECT_METHODS)) {

						ClassType thisType = context.getClassinfo().getThisType();

						if (lambdaDescriptor.equalsIgnoreClass(STRING, "toString", thisType))
							return RecordSyntheticOperation.TO_STRING;

						if (lambdaDescriptor.equalsIgnoreClass(PrimitiveType.INT, "hashCode", thisType))
							return RecordSyntheticOperation.HASH_CODE;

						if (lambdaDescriptor.equalsIgnoreClass(PrimitiveType.BOOLEAN, "equals", thisType, OBJECT))
							return RecordSyntheticOperation.EQUALS;
					}
				}

				throw new DecompilationException("invokedynamic instruction is not recognized: " +
						"referenceKind = " + methodHandle.getReferenceKind() + ", " +
						"lambdaDescriptor = " + lambdaDescriptor + ", " +
						"invokedynamicDescriptor = " + invokedynamicDescriptor);
			}
		}
	}
}
