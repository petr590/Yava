package x590.yava.io;

import x590.util.io.UncheckedDataOutputStream;

import java.io.OutputStream;

import static x590.yava.context.Opcodes.LDC;
import static x590.yava.context.Opcodes.LDC_W;

public class BytecodeOutputStream extends UncheckedDataOutputStream<BytecodeOutputStream> {

	public BytecodeOutputStream(OutputStream out) {
		super(out);
	}

	public void writeOpcodeWithByte(int value, int opcode) {
		recordByte(opcode).writeByte(value);
	}

	public void writeOpcodeWithShort(int value, int opcode) {
		recordByte(opcode).writeShort(value);
	}

	public void writeOpcodeWithInt(int value, int opcode) {
		recordByte(opcode).writeInt(value);
	}

	public void writeLdcOrLdcW(int index) {
		if ((byte)index != index) {
			writeOpcodeWithShort(index, LDC_W);
		} else {
			writeOpcodeWithByte(index, LDC);
		}
	}
}
