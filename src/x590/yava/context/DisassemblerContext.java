package x590.yava.context;

import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import x590.util.IntegerUtil;
import x590.util.Logger;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.disassembling.InstructionFormatException;
import x590.yava.exception.disassembling.InvalidOpcodeException;
import x590.yava.instruction.Instruction;
import x590.yava.instruction.Instructions;
import x590.yava.instruction.array.ANewArrayInstruction;
import x590.yava.instruction.array.MultiANewArrayInstruction;
import x590.yava.instruction.array.NewArrayInstruction;
import x590.yava.instruction.cast.CheckCastInstruction;
import x590.yava.instruction.constant.IConstInstruction;
import x590.yava.instruction.constant.LdcInstruction;
import x590.yava.instruction.field.GetInstanceFieldInstruction;
import x590.yava.instruction.field.GetStaticFieldInstruction;
import x590.yava.instruction.field.PutInstanceFieldInstruction;
import x590.yava.instruction.field.PutStaticFieldInstruction;
import x590.yava.instruction.increment.IIncInstruction;
import x590.yava.instruction.invoke.*;
import x590.yava.instruction.load.*;
import x590.yava.instruction.other.InstanceofInstruction;
import x590.yava.instruction.other.NewInstruction;
import x590.yava.instruction.scope.*;
import x590.yava.instruction.store.*;
import x590.yava.type.TypeSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DisassemblerContext extends Context {

	public static final byte[] EMPTY_DATA = {};

	private final byte[] bytes;

	private int pos;
	private final @Immutable List<Instruction> instructions;

	private DisassemblerContext(ConstantPool pool, byte[] bytes) {
		super(pool, bytes.length);
		this.bytes = bytes;

		var length = bytes.length;

		if (length == 0) {
			this.instructions = Collections.emptyList();
			return;
		}

		List<Instruction> instructions = new ArrayList<>(length);

		while (pos < length) {
			indexMap.put(pos, index);
			posMap.put(index, pos);

			Instruction instruction = readInstruction();

			if (instruction != null) {
				instructions.add(instruction);
				index++;
			}

			pos++;
		}

		this.instructions = Collections.unmodifiableList(instructions);
	}


	public static DisassemblerContext disassemble(ConstantPool pool, byte[] bytes) {
		return new DisassemblerContext(pool, bytes);
	}

	public static DisassemblerContext empty(ConstantPool pool) {
		return new DisassemblerContext(pool, EMPTY_DATA);
	}


	public @Immutable List<Instruction> getInstructions() {
		return instructions;
	}


	private int readByte() {
		return bytes[pos += 1];
	}

	private int readShort() {
		return (short) ((bytes[pos + 1] & 0xFF) << 8 | bytes[pos += 2] & 0xFF);
	}

	private int readInt() {
		return (bytes[pos + 1] & 0xFF) << 24 | (bytes[pos + 2] & 0xFF) << 16 |
				(bytes[pos + 3] & 0xFF) << 8 | bytes[pos += 4] & 0xFF;
	}

	private int readUnsignedByte() {
		return readByte() & 0xFF;
	}

	private int readUnsignedShort() {
		return readShort() & 0xFFFF;
	}

	private void skip(int count) {
		pos += count;
	}


	private @Nullable Instruction readInstruction() {
		return switch (bytes[pos] & 0xFF) {
			case 0x00 -> null;
			case 0x01 -> Instructions.ACONST_NULL;
			case 0x02 -> Instructions.ICONST_M1;
			case 0x03 -> Instructions.ICONST_0;
			case 0x04 -> Instructions.ICONST_1;
			case 0x05 -> Instructions.ICONST_2;
			case 0x06 -> Instructions.ICONST_3;
			case 0x07 -> Instructions.ICONST_4;
			case 0x08 -> Instructions.ICONST_5;
			case 0x09 -> Instructions.LCONST_0;
			case 0x0A -> Instructions.LCONST_1;
			case 0x0B -> Instructions.FCONST_0;
			case 0x0C -> Instructions.FCONST_1;
			case 0x0D -> Instructions.FCONST_2;
			case 0x0E -> Instructions.DCONST_0;
			case 0x0F -> Instructions.DCONST_1;
			case 0x10 -> new IConstInstruction(readByte());
			case 0x11 -> new IConstInstruction(readShort());
			case 0x12 -> new LdcInstruction(TypeSize.WORD, this, readUnsignedByte());
			case 0x13 -> new LdcInstruction(TypeSize.WORD, this, readUnsignedShort());
			case 0x14 -> new LdcInstruction(TypeSize.LONG, this, readUnsignedShort());

			case 0x15 -> new ILoadInstruction(readUnsignedByte());
			case 0x16 -> new LLoadInstruction(readUnsignedByte());
			case 0x17 -> new FLoadInstruction(readUnsignedByte());
			case 0x18 -> new DLoadInstruction(readUnsignedByte());
			case 0x19 -> new ALoadInstruction(readUnsignedByte());
			case 0x1A -> Instructions.ILOAD_0;
			case 0x1B -> Instructions.ILOAD_1;
			case 0x1C -> Instructions.ILOAD_2;
			case 0x1D -> Instructions.ILOAD_3;
			case 0x1E -> Instructions.LLOAD_0;
			case 0x1F -> Instructions.LLOAD_1;
			case 0x20 -> Instructions.LLOAD_2;
			case 0x21 -> Instructions.LLOAD_3;
			case 0x22 -> Instructions.FLOAD_0;
			case 0x23 -> Instructions.FLOAD_1;
			case 0x24 -> Instructions.FLOAD_2;
			case 0x25 -> Instructions.FLOAD_3;
			case 0x26 -> Instructions.DLOAD_0;
			case 0x27 -> Instructions.DLOAD_1;
			case 0x28 -> Instructions.DLOAD_2;
			case 0x29 -> Instructions.DLOAD_3;
			case 0x2A -> Instructions.ALOAD_0;
			case 0x2B -> Instructions.ALOAD_1;
			case 0x2C -> Instructions.ALOAD_2;
			case 0x2D -> Instructions.ALOAD_3;

			case 0x2E -> Instructions.IALOAD;
			case 0x2F -> Instructions.LALOAD;
			case 0x30 -> Instructions.FALOAD;
			case 0x31 -> Instructions.DALOAD;
			case 0x32 -> Instructions.AALOAD;
			case 0x33 -> Instructions.BALOAD;
			case 0x34 -> Instructions.CALOAD;
			case 0x35 -> Instructions.SALOAD;

			case 0x36 -> new IStoreInstruction(readUnsignedByte());
			case 0x37 -> new LStoreInstruction(readUnsignedByte());
			case 0x38 -> new FStoreInstruction(readUnsignedByte());
			case 0x39 -> new DStoreInstruction(readUnsignedByte());
			case 0x3A -> new AStoreInstruction(readUnsignedByte());
			case 0x3B -> Instructions.ISTORE_0;
			case 0x3C -> Instructions.ISTORE_1;
			case 0x3D -> Instructions.ISTORE_2;
			case 0x3E -> Instructions.ISTORE_3;
			case 0x3F -> Instructions.LSTORE_0;
			case 0x40 -> Instructions.LSTORE_1;
			case 0x41 -> Instructions.LSTORE_2;
			case 0x42 -> Instructions.LSTORE_3;
			case 0x43 -> Instructions.FSTORE_0;
			case 0x44 -> Instructions.FSTORE_1;
			case 0x45 -> Instructions.FSTORE_2;
			case 0x46 -> Instructions.FSTORE_3;
			case 0x47 -> Instructions.DSTORE_0;
			case 0x48 -> Instructions.DSTORE_1;
			case 0x49 -> Instructions.DSTORE_2;
			case 0x4A -> Instructions.DSTORE_3;
			case 0x4B -> Instructions.ASTORE_0;
			case 0x4C -> Instructions.ASTORE_1;
			case 0x4D -> Instructions.ASTORE_2;
			case 0x4E -> Instructions.ASTORE_3;

			case 0x4F -> Instructions.IASTORE;
			case 0x50 -> Instructions.LASTORE;
			case 0x51 -> Instructions.FASTORE;
			case 0x52 -> Instructions.DASTORE;
			case 0x53 -> Instructions.AASTORE;
			case 0x54 -> Instructions.BASTORE;
			case 0x55 -> Instructions.CASTORE;
			case 0x56 -> Instructions.SASTORE;

			case 0x57 -> Instructions.POP;
			case 0x58 -> Instructions.POP2;
			case 0x59 -> Instructions.DUP;
			case 0x5A -> Instructions.DUP_X1;
			case 0x5B -> Instructions.DUP_X2;
			case 0x5C -> Instructions.DUP2;
			case 0x5D -> Instructions.DUP2_X1;
			case 0x5E -> Instructions.DUP2_X2;
			case 0x5F -> Instructions.SWAP;

			case 0x60 -> Instructions.IADD;
			case 0x61 -> Instructions.LADD;
			case 0x62 -> Instructions.FADD;
			case 0x63 -> Instructions.DADD;
			case 0x64 -> Instructions.ISUB;
			case 0x65 -> Instructions.LSUB;
			case 0x66 -> Instructions.FSUB;
			case 0x67 -> Instructions.DSUB;
			case 0x68 -> Instructions.IMUL;
			case 0x69 -> Instructions.LMUL;
			case 0x6A -> Instructions.FMUL;
			case 0x6B -> Instructions.DMUL;
			case 0x6C -> Instructions.IDIV;
			case 0x6D -> Instructions.LDIV;
			case 0x6E -> Instructions.FDIV;
			case 0x6F -> Instructions.DDIV;
			case 0x70 -> Instructions.IREM;
			case 0x71 -> Instructions.LREM;
			case 0x72 -> Instructions.FREM;
			case 0x73 -> Instructions.DREM;
			case 0x74 -> Instructions.INEG;
			case 0x75 -> Instructions.LNEG;
			case 0x76 -> Instructions.FNEG;
			case 0x77 -> Instructions.DNEG;

			case 0x78 -> Instructions.ISHL;
			case 0x79 -> Instructions.LSHL;
			case 0x7A -> Instructions.ISHR;
			case 0x7B -> Instructions.LSHR;
			case 0x7C -> Instructions.IUSHR;
			case 0x7D -> Instructions.LUSHR;
			case 0x7E -> Instructions.IAND;
			case 0x7F -> Instructions.LAND;
			case 0x80 -> Instructions.IOR;
			case 0x81 -> Instructions.LOR;
			case 0x82 -> Instructions.IXOR;
			case 0x83 -> Instructions.LXOR;

			case 0x84 -> new IIncInstruction(readUnsignedByte(), readByte());

			case 0x85 -> Instructions.I2L;
			case 0x86 -> Instructions.I2F;
			case 0x87 -> Instructions.I2D;
			case 0x88 -> Instructions.L2I;
			case 0x89 -> Instructions.L2F;
			case 0x8A -> Instructions.L2D;
			case 0x8B -> Instructions.F2I;
			case 0x8C -> Instructions.F2L;
			case 0x8D -> Instructions.F2D;
			case 0x8E -> Instructions.D2I;
			case 0x8F -> Instructions.D2L;
			case 0x90 -> Instructions.D2F;
			case 0x91 -> Instructions.I2B;
			case 0x92 -> Instructions.I2C;
			case 0x93 -> Instructions.I2S;

			case 0x94 -> Instructions.LCMP;
			case 0x95, 0x96 -> Instructions.FCMP;
			case 0x97, 0x98 -> Instructions.DCMP;

			case 0x99 -> new IfEqInstruction(this, readShort());
			case 0x9A -> new IfNotEqInstruction(this, readShort());
			case 0x9B -> new IfLtInstruction(this, readShort());
			case 0x9C -> new IfGeInstruction(this, readShort());
			case 0x9D -> new IfGtInstruction(this, readShort());
			case 0x9E -> new IfLeInstruction(this, readShort());
			case 0x9F -> new IfIEqInstruction(this, readShort());
			case 0xA0 -> new IfINotEqInstruction(this, readShort());
			case 0xA1 -> new IfILtInstruction(this, readShort());
			case 0xA2 -> new IfIGeInstruction(this, readShort());
			case 0xA3 -> new IfIGtInstruction(this, readShort());
			case 0xA4 -> new IfILeInstruction(this, readShort());
			case 0xA5 -> new IfAEqInstruction(this, readShort());
			case 0xA6 -> new IfANotEqInstruction(this, readShort());
			case 0xA7 -> new GotoInstruction(this, readShort());

//			case 0xA8 -> jsr(readShort());
//			case 0xA9 -> ret(readUnsignedByte());

			case 0xAA -> {
				skip(3 - (pos & 0x3)); // alignment by 4 bytes

				int defaultOffset = readInt(),
						low = readInt(),
						high = readInt();

				if (high < low)
					throw new InstructionFormatException("tableswitch: high < low (low = " + low + ", high = " + high + ")");

				Int2IntMap offsetTable = new Int2IntLinkedOpenHashMap(high - low);

				for (int value = low; value <= high; ++value) {
					offsetTable.put(value, readInt());
				}

				yield new SwitchInstruction(this, defaultOffset, offsetTable);
			}

			case 0xAB -> {
				skip(3 - (pos & 0x3)); // alignment by 4 bytes

				int defaultOffset = readInt();
				int cases = readInt();

				Int2IntMap offsetTable = new Int2IntLinkedOpenHashMap(cases);

				for (; cases != 0; --cases) {
					offsetTable.put(readInt(), readInt());
				}

				yield new SwitchInstruction(this, defaultOffset, offsetTable);
			}

			case 0xAC -> Instructions.IRETURN;
			case 0xAD -> Instructions.LRETURN;
			case 0xAE -> Instructions.FRETURN;
			case 0xAF -> Instructions.DRETURN;
			case 0xB0 -> Instructions.ARETURN;
			case 0xB1 -> Instructions.VRETURN;

			case 0xB2 -> new GetStaticFieldInstruction(readUnsignedShort());
			case 0xB3 -> new PutStaticFieldInstruction(readUnsignedShort());
			case 0xB4 -> new GetInstanceFieldInstruction(readUnsignedShort());
			case 0xB5 -> new PutInstanceFieldInstruction(readUnsignedShort());

			case 0xB6 -> new InvokevirtualInstruction(readUnsignedShort());
			case 0xB7 -> new InvokespecialInstruction(readUnsignedShort());
			case 0xB8 -> new InvokestaticInstruction(readUnsignedShort());
			case 0xB9 ->
					new InvokeinterfaceInstruction(this, readUnsignedShort(), readUnsignedByte(), readUnsignedByte());
			case 0xBA -> new InvokedynamicInstruction(this, readUnsignedShort(), readUnsignedShort());

			case 0xBB -> new NewInstruction(readUnsignedShort());
			case 0xBC -> new NewArrayInstruction(readUnsignedByte());
			case 0xBD -> new ANewArrayInstruction(readUnsignedShort());
			case 0xBE -> Instructions.ARRAYLENGTH;

			case 0xBF -> Instructions.ATHROW;
			case 0xC0 -> new CheckCastInstruction(readUnsignedShort());
			case 0xC1 -> new InstanceofInstruction(readUnsignedShort());

			case 0xC2 -> Instructions.MONITORENTER;
			case 0xC3 -> Instructions.MONITOREXIT;

			case 0xC4 -> switch (readUnsignedByte()) {
				case 0x15 -> new ILoadInstruction(readUnsignedShort());
				case 0x16 -> new LLoadInstruction(readUnsignedShort());
				case 0x17 -> new FLoadInstruction(readUnsignedShort());
				case 0x18 -> new DLoadInstruction(readUnsignedShort());
				case 0x19 -> new ALoadInstruction(readUnsignedShort());
				case 0x36 -> new IStoreInstruction(readUnsignedShort());
				case 0x37 -> new LStoreInstruction(readUnsignedShort());
				case 0x38 -> new FStoreInstruction(readUnsignedShort());
				case 0x39 -> new DStoreInstruction(readUnsignedShort());
				case 0x3A -> new AStoreInstruction(readUnsignedShort());
				case 0x84 -> new IIncInstruction(readUnsignedShort(), readShort());
//					case 0xA9 -> ret(readUnsignedShort());
				default -> throw new InvalidOpcodeException("Illegal wide opcode 0x" + IntegerUtil.hex2(bytes[pos]));
			};

			case 0xC5 -> new MultiANewArrayInstruction(readUnsignedShort(), readUnsignedByte());

			case 0xC6 -> new IfNullInstruction(this, readShort());
			case 0xC7 -> new IfNonNullInstruction(this, readShort());
			case 0xC8 -> new GotoInstruction(this, readInt());
			/*case 0xC9 -> jsr_w(readInt());
			case 0xCA -> breakpoint;
			case 0xFE -> impdep1;
			case 0xFF -> impdep2;*/
			default -> throw new InvalidOpcodeException(IntegerUtil.hex2WithPrefix(bytes[pos]));
		};
	}


	@Override
	public void warning(String message) {
		Logger.warning("Disassembling warning " + message);
	}
}
