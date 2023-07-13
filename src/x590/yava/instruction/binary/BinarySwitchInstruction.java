package x590.yava.instruction.binary;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.io.DisassemblingOutputStream;

public class BinarySwitchInstruction extends BinaryInstructionWithMnemonic {

	private final int defaultPos;

	// key: constant, value: pos
	private final Int2IntMap posTable;

	public BinarySwitchInstruction(String mnemonic, BytecodeDisassemblingContext context, int defaultPos, Int2IntMap posTable) {
		super(mnemonic);

		this.defaultPos = defaultPos;
		this.posTable = posTable;

		context.declareLabelAt(defaultPos);
		posTable.values().forEach(context::declareLabelAt);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, BytecodeDisassemblingContext context) {
		out.printsp(mnemonic).println('{').increaseIndent();

		posTable.int2IntEntrySet()
				.forEach(entry -> out.printIndent().printsp("case").printInt(entry.getIntKey()).printsp(':').println(context.labelAt(entry.getIntValue())));

		out .printIndent().printsp("default:").println(context.labelAt(defaultPos))
			.reduceIndent().printIndent().println('}');
	}
}
