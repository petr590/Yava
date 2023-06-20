package x590.yava.instruction.array;

import x590.util.IntegerUtil;
import x590.yava.context.DecompilationContext;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.instruction.Instruction;
import x590.yava.operation.Operation;
import x590.yava.operation.array.NewArrayOperation;
import x590.yava.type.reference.ArrayType;

public class NewArrayInstruction implements Instruction {

	private static ArrayType getArrayTypeByCode(int code) {
		switch (code) {
			case 0x4:
				return ArrayType.BOOLEAN_ARRAY;
			case 0x5:
				return ArrayType.CHAR_ARRAY;
			case 0x6:
				return ArrayType.FLOAT_ARRAY;
			case 0x7:
				return ArrayType.DOUBLE_ARRAY;
			case 0x8:
				return ArrayType.BYTE_ARRAY;
			case 0x9:
				return ArrayType.SHORT_ARRAY;
			case 0xA:
				return ArrayType.INT_ARRAY;
			case 0xB:
				return ArrayType.LONG_ARRAY;
			default:
				throw new DecompilationException("Illegal array type code 0x" + IntegerUtil.hex(code));
		}
	}


	private final ArrayType type;

	public NewArrayInstruction(int code) {
		this.type = getArrayTypeByCode(code);
	}

	@Override
	public Operation toOperation(DecompilationContext context) {
		return new NewArrayOperation(context, type);
	}
}
