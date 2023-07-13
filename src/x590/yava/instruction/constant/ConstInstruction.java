package x590.yava.instruction.constant;

import x590.yava.constpool.constvalue.ConstValueConstant;
import x590.yava.instruction.binary.SimpleInstruction;

public abstract class ConstInstruction<C extends ConstValueConstant> implements SimpleInstruction {

	protected final C constant;

	public ConstInstruction(C constant) {
		this.constant = constant;
	}
}
