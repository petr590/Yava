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

import static x590.yava.context.Opcodes.*;

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
		return (short)((bytes[pos + 1] & 0xFF) << 8 | bytes[pos += 2] & 0xFF);
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
			case NOP -> null;
			case ACONST_NULL -> Instructions.ACONST_NULL;
			case ICONST_M1 -> Instructions.ICONST_M1;
			case ICONST_0  -> Instructions.ICONST_0;
			case ICONST_1  -> Instructions.ICONST_1;
			case ICONST_2  -> Instructions.ICONST_2;
			case ICONST_3  -> Instructions.ICONST_3;
			case ICONST_4  -> Instructions.ICONST_4;
			case ICONST_5  -> Instructions.ICONST_5;
			case LCONST_0  -> Instructions.LCONST_0;
			case LCONST_1  -> Instructions.LCONST_1;
			case FCONST_0  -> Instructions.FCONST_0;
			case FCONST_1  -> Instructions.FCONST_1;
			case FCONST_2  -> Instructions.FCONST_2;
			case DCONST_0  -> Instructions.DCONST_0;
			case DCONST_1  -> Instructions.DCONST_1;
			case BIPUSH -> IConstInstruction.of(readByte());
			case SIPUSH -> IConstInstruction.of(readShort());
			case LDC    -> new LdcInstruction(TypeSize.WORD, readUnsignedByte());
			case LDC_W  -> new LdcInstruction(TypeSize.WORD, readUnsignedShort());
			case LDC2_W -> new LdcInstruction(TypeSize.LONG, readUnsignedShort());

			case ILOAD -> ILoadInstruction.of(readUnsignedByte());
			case LLOAD -> LLoadInstruction.of(readUnsignedByte());
			case FLOAD -> FLoadInstruction.of(readUnsignedByte());
			case DLOAD -> DLoadInstruction.of(readUnsignedByte());
			case ALOAD -> ALoadInstruction.of(readUnsignedByte());
			case ILOAD_0 -> Instructions.ILOAD_0;
			case ILOAD_1 -> Instructions.ILOAD_1;
			case ILOAD_2 -> Instructions.ILOAD_2;
			case ILOAD_3 -> Instructions.ILOAD_3;
			case LLOAD_0 -> Instructions.LLOAD_0;
			case LLOAD_1 -> Instructions.LLOAD_1;
			case LLOAD_2 -> Instructions.LLOAD_2;
			case LLOAD_3 -> Instructions.LLOAD_3;
			case FLOAD_0 -> Instructions.FLOAD_0;
			case FLOAD_1 -> Instructions.FLOAD_1;
			case FLOAD_2 -> Instructions.FLOAD_2;
			case FLOAD_3 -> Instructions.FLOAD_3;
			case DLOAD_0 -> Instructions.DLOAD_0;
			case DLOAD_1 -> Instructions.DLOAD_1;
			case DLOAD_2 -> Instructions.DLOAD_2;
			case DLOAD_3 -> Instructions.DLOAD_3;
			case ALOAD_0 -> Instructions.ALOAD_0;
			case ALOAD_1 -> Instructions.ALOAD_1;
			case ALOAD_2 -> Instructions.ALOAD_2;
			case ALOAD_3 -> Instructions.ALOAD_3;

			case IALOAD -> Instructions.IALOAD;
			case LALOAD -> Instructions.LALOAD;
			case FALOAD -> Instructions.FALOAD;
			case DALOAD -> Instructions.DALOAD;
			case AALOAD -> Instructions.AALOAD;
			case BALOAD -> Instructions.BALOAD;
			case CALOAD -> Instructions.CALOAD;
			case SALOAD -> Instructions.SALOAD;

			case ISTORE -> IStoreInstruction.of(readUnsignedByte());
			case LSTORE -> LStoreInstruction.of(readUnsignedByte());
			case FSTORE -> FStoreInstruction.of(readUnsignedByte());
			case DSTORE -> DStoreInstruction.of(readUnsignedByte());
			case ASTORE -> AStoreInstruction.of(readUnsignedByte());
			case ISTORE_0 -> Instructions.ISTORE_0;
			case ISTORE_1 -> Instructions.ISTORE_1;
			case ISTORE_2 -> Instructions.ISTORE_2;
			case ISTORE_3 -> Instructions.ISTORE_3;
			case LSTORE_0 -> Instructions.LSTORE_0;
			case LSTORE_1 -> Instructions.LSTORE_1;
			case LSTORE_2 -> Instructions.LSTORE_2;
			case LSTORE_3 -> Instructions.LSTORE_3;
			case FSTORE_0 -> Instructions.FSTORE_0;
			case FSTORE_1 -> Instructions.FSTORE_1;
			case FSTORE_2 -> Instructions.FSTORE_2;
			case FSTORE_3 -> Instructions.FSTORE_3;
			case DSTORE_0 -> Instructions.DSTORE_0;
			case DSTORE_1 -> Instructions.DSTORE_1;
			case DSTORE_2 -> Instructions.DSTORE_2;
			case DSTORE_3 -> Instructions.DSTORE_3;
			case ASTORE_0 -> Instructions.ASTORE_0;
			case ASTORE_1 -> Instructions.ASTORE_1;
			case ASTORE_2 -> Instructions.ASTORE_2;
			case ASTORE_3 -> Instructions.ASTORE_3;

			case IASTORE -> Instructions.IASTORE;
			case LASTORE -> Instructions.LASTORE;
			case FASTORE -> Instructions.FASTORE;
			case DASTORE -> Instructions.DASTORE;
			case AASTORE -> Instructions.AASTORE;
			case BASTORE -> Instructions.BASTORE;
			case CASTORE -> Instructions.CASTORE;
			case SASTORE -> Instructions.SASTORE;

			case POP     -> Instructions.POP;
			case POP2    -> Instructions.POP2;
			case DUP     -> Instructions.DUP;
			case DUP_X1  -> Instructions.DUP_X1;
			case DUP_X2  -> Instructions.DUP_X2;
			case DUP2    -> Instructions.DUP2;
			case DUP2_X1 -> Instructions.DUP2_X1;
			case DUP2_X2 -> Instructions.DUP2_X2;
			case SWAP    -> Instructions.SWAP;

			case IADD -> Instructions.IADD;
			case LADD -> Instructions.LADD;
			case FADD -> Instructions.FADD;
			case DADD -> Instructions.DADD;
			case ISUB -> Instructions.ISUB;
			case LSUB -> Instructions.LSUB;
			case FSUB -> Instructions.FSUB;
			case DSUB -> Instructions.DSUB;
			case IMUL -> Instructions.IMUL;
			case LMUL -> Instructions.LMUL;
			case FMUL -> Instructions.FMUL;
			case DMUL -> Instructions.DMUL;
			case IDIV -> Instructions.IDIV;
			case LDIV -> Instructions.LDIV;
			case FDIV -> Instructions.FDIV;
			case DDIV -> Instructions.DDIV;
			case IREM -> Instructions.IREM;
			case LREM -> Instructions.LREM;
			case FREM -> Instructions.FREM;
			case DREM -> Instructions.DREM;
			case INEG -> Instructions.INEG;
			case LNEG -> Instructions.LNEG;
			case FNEG -> Instructions.FNEG;
			case DNEG -> Instructions.DNEG;

			case ISHL -> Instructions.ISHL;
			case LSHL -> Instructions.LSHL;
			case ISHR -> Instructions.ISHR;
			case LSHR -> Instructions.LSHR;
			case IUSHR -> Instructions.IUSHR;
			case LUSHR -> Instructions.LUSHR;
			case IAND -> Instructions.IAND;
			case LAND -> Instructions.LAND;
			case IOR -> Instructions.IOR;
			case LOR -> Instructions.LOR;
			case IXOR -> Instructions.IXOR;
			case LXOR -> Instructions.LXOR;

			case IINC -> new IIncInstruction(readUnsignedByte(), readByte());

			case I2L -> Instructions.I2L;
			case I2F -> Instructions.I2F;
			case I2D -> Instructions.I2D;
			case L2I -> Instructions.L2I;
			case L2F -> Instructions.L2F;
			case L2D -> Instructions.L2D;
			case F2I -> Instructions.F2I;
			case F2L -> Instructions.F2L;
			case F2D -> Instructions.F2D;
			case D2I -> Instructions.D2I;
			case D2L -> Instructions.D2L;
			case D2F -> Instructions.D2F;
			case I2B -> Instructions.I2B;
			case I2C -> Instructions.I2C;
			case I2S -> Instructions.I2S;

			case LCMP -> Instructions.LCMP;
			case FCMPL -> Instructions.FCMPL;
			case FCMPG -> Instructions.FCMPG;
			case DCMPL -> Instructions.DCMPL;
			case DCMPG -> Instructions.DCMPG;

			case IFEQ -> new IfEqInstruction(this, readShort());
			case IFNE -> new IfNotEqInstruction(this, readShort());
			case IFLT -> new IfLtInstruction(this, readShort());
			case IFGE -> new IfGeInstruction(this, readShort());
			case IFGT -> new IfGtInstruction(this, readShort());
			case IFLE -> new IfLeInstruction(this, readShort());
			case IF_ICMPEQ -> new IfIEqInstruction(this, readShort());
			case IF_ICMPNE -> new IfINotEqInstruction(this, readShort());
			case IF_ICMPLT -> new IfILtInstruction(this, readShort());
			case IF_ICMPGE -> new IfIGeInstruction(this, readShort());
			case IF_ICMPGT -> new IfIGtInstruction(this, readShort());
			case IF_ICMPLE -> new IfILeInstruction(this, readShort());
			case IF_ACMPEQ -> new IfAEqInstruction(this, readShort());
			case IF_ACMPNE -> new IfANotEqInstruction(this, readShort());
			case GOTO -> new GotoInstruction(this, readShort());

//			case JSR -> jsr(readShort());
//			case RET -> ret(readUnsignedByte());

			case TABLESWITCH -> {
				skip(3 - (pos & 0x3)); // alignment by 4 bytes

				int defaultOffset = readInt(),
						low = readInt(),
						high = readInt();

				if (high < low)
					throw new InstructionFormatException("tableswitch: high < low (low = " + low + ", high = " + high + ")");

				Int2IntMap offsetTable = new Int2IntLinkedOpenHashMap(high - low);

				for (int value = low; value <= high; value++) {
					offsetTable.put(value, readInt());
				}

				yield new SwitchInstruction(this, defaultOffset, offsetTable);
			}

			case LOOKUPSWITCH -> {
				skip(3 - (pos & 0x3)); // alignment by 4 bytes

				int defaultOffset = readInt();
				int cases = readInt();

				Int2IntMap offsetTable = new Int2IntLinkedOpenHashMap(cases);

				for (; cases != 0; cases--) {
					offsetTable.put(readInt(), readInt());
				}

				yield new SwitchInstruction(this, defaultOffset, offsetTable);
			}

			case IRETURN -> Instructions.IRETURN;
			case LRETURN -> Instructions.LRETURN;
			case FRETURN -> Instructions.FRETURN;
			case DRETURN -> Instructions.DRETURN;
			case ARETURN -> Instructions.ARETURN;
			case RETURN  -> Instructions.VRETURN;

			case GETSTATIC -> new GetStaticFieldInstruction(readUnsignedShort());
			case PUTSTATIC -> new PutStaticFieldInstruction(readUnsignedShort());
			case GETFIELD  -> new GetInstanceFieldInstruction(readUnsignedShort());
			case PUTFIELD  -> new PutInstanceFieldInstruction(readUnsignedShort());

			case INVOKEVIRTUAL   -> new InvokevirtualInstruction(readUnsignedShort());
			case INVOKESPECIAL   -> new InvokespecialInstruction(readUnsignedShort());
			case INVOKESTATIC    -> new InvokestaticInstruction(readUnsignedShort());
			case INVOKEINTERFACE -> new InvokeinterfaceInstruction(this, readUnsignedShort(), readUnsignedByte(), readUnsignedByte());
			case INVOKEDYNAMIC   -> new InvokedynamicInstruction(this, readUnsignedShort(), readUnsignedShort());

			case NEW         -> new NewInstruction(readUnsignedShort());
			case NEWARRAY    -> new NewArrayInstruction(readUnsignedByte());
			case ANEWARRAY   -> new ANewArrayInstruction(readUnsignedShort());
			case ARRAYLENGTH -> Instructions.ARRAYLENGTH;

			case ATHROW     -> Instructions.ATHROW;
			case CHECKCAST  -> new CheckCastInstruction(readUnsignedShort());
			case INSTANCEOF -> new InstanceofInstruction(readUnsignedShort());

			case MONITORENTER -> Instructions.MONITORENTER;
			case MONITOREXIT  -> Instructions.MONITOREXIT;

			case WIDE -> switch (readUnsignedByte()) {
				case ILOAD -> ILoadInstruction.of(readUnsignedShort());
				case LLOAD -> LLoadInstruction.of(readUnsignedShort());
				case FLOAD -> FLoadInstruction.of(readUnsignedShort());
				case DLOAD -> DLoadInstruction.of(readUnsignedShort());
				case ALOAD -> ALoadInstruction.of(readUnsignedShort());
				case ISTORE -> IStoreInstruction.of(readUnsignedShort());
				case LSTORE -> LStoreInstruction.of(readUnsignedShort());
				case FSTORE -> FStoreInstruction.of(readUnsignedShort());
				case DSTORE -> DStoreInstruction.of(readUnsignedShort());
				case ASTORE -> AStoreInstruction.of(readUnsignedShort());
				case IINC -> new IIncInstruction(readUnsignedShort(), readShort());
//				case RET -> ret(readUnsignedShort());
				default -> throw new InvalidOpcodeException("Illegal wide opcode 0x" + IntegerUtil.hex2(bytes[pos]));
			};

			case MULTIANEWARRAY -> new MultiANewArrayInstruction(readUnsignedShort(), readUnsignedByte());

			case IFNULL    -> new IfNullInstruction(this, readShort());
			case IFNONNULL -> new IfNonNullInstruction(this, readShort());
			case GOTO_W    -> new GotoInstruction(this, readInt());
//			case JSR_W -> jsr_w(readInt());
			default -> throw new InvalidOpcodeException(IntegerUtil.hex2WithPrefix(bytes[pos]));
		};
	}


	@Override
	public void warning(String message) {
		Logger.warning("Disassembling warning " + message);
	}
}
