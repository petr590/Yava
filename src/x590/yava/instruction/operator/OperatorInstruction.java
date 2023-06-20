package x590.yava.instruction.operator;

import x590.yava.instruction.Instruction;
import x590.yava.type.Type;

public abstract class OperatorInstruction implements Instruction {

	protected final Type type;

	public OperatorInstruction(Type type) {
		this.type = type;
	}
}
