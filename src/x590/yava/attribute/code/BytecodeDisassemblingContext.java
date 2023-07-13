package x590.yava.attribute.code;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.util.Logger;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.instruction.binary.BinaryInstruction;
import x590.yava.io.BytecodeInputStream;
import x590.yava.io.DisassemblingOutputStream;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class BytecodeDisassemblingContext {

	private final ClassInfo classinfo;

	private final Int2ObjectMap<String> labels = new Int2ObjectArrayMap<>();
	private int lastLabelNumber;

	private final List<BinaryInstruction> instructions = new ArrayList<>();

	private final Int2IntMap indexToPosTable = new Int2IntArrayMap();

	private int pos;

	private final BytecodeInputStream in;


	private BytecodeDisassemblingContext(ClassInfo classinfo, byte[] code) {
		this.classinfo = classinfo;

		this.in = new BytecodeInputStream(new ByteArrayInputStream(code));

		for (int index = 0; in.available() > 0; index++) {
			pos = in.getIntPosition();
			indexToPosTable.put(index, pos);
			instructions.add(in.readInstruction(this));
		}
	}

	public static BytecodeDisassemblingContext disassemble(ClassInfo classinfo, byte[] code) {
		return new BytecodeDisassemblingContext(classinfo, code);
	}

	public ClassInfo getClassInfo() {
		return classinfo;
	}

	public ConstantPool getConstPool() {
		return classinfo.getConstPool();
	}

	public int currentPos() {
		return pos;
	}

	/** Объявляет лейбл на позиции {@code pos} */
	public void declareLabelAt(int pos) {
		labels.computeIfAbsent(pos, key -> "L" + ++lastLabelNumber);
	}

	/** @return Лейбл на позиции {@code pos}
	 * @throws IllegalStateException если лейбл на указанной позиции не найден */
	public String labelAt(int pos) {
		return labels.computeIfAbsent(pos, key -> {
			throw new IllegalStateException("Label at pos " + pos + " is not found");
		});
	}

	public void writeDisassembled(DisassemblingOutputStream out) {
		out.println();

		final var instructions = this.instructions;

		for (int i = 0, size = instructions.size(); i < size; i++) {
			String label = labels.get(indexToPosTable.get(i));

			if (label != null) {
				out.reduceIndent().printIndent().print(label).println(':').increaseIndent();
			}

			out.printIndent().println(instructions.get(i), this);
		}

		out.println();
	}
}
