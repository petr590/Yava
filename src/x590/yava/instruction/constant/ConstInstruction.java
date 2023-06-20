package x590.yava.instruction.constant;

import x590.yava.constpool.ConstValueConstant;
import x590.yava.instruction.Instruction;

public abstract class ConstInstruction<C extends ConstValueConstant> implements Instruction {

	protected final C constant;

	public ConstInstruction(C constant) {
		this.constant = constant;
	}
}
