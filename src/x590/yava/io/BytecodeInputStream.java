package x590.yava.io;

import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import x590.util.IntegerUtil;
import x590.util.io.UncheckedDataInputStream;
import x590.util.io.UncheckedPositionedInput;
import x590.util.io.UncheckedPositionedInputStream;
import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.constpool.constvalue.ClassConstant;
import x590.yava.constpool.constvalue.ConstValueConstant;
import x590.yava.constpool.FieldrefConstant;
import x590.yava.constpool.MethodrefConstant;
import x590.yava.exception.disassembling.InstructionFormatException;
import x590.yava.exception.disassembling.InvalidOpcodeException;
import x590.yava.instruction.Instructions;
import x590.yava.instruction.binary.*;
import x590.yava.instruction.constant.IConstInstruction;
import x590.yava.instruction.increment.IIncInstruction;
import x590.yava.instruction.load.*;
import x590.yava.instruction.store.*;

import java.io.InputStream;

import static x590.yava.context.Opcodes.*;

public class BytecodeInputStream extends UncheckedDataInputStream implements UncheckedPositionedInput {

	private final UncheckedPositionedInput positioned;

	public BytecodeInputStream(InputStream in) {
		this(in instanceof UncheckedPositionedInputStream positionedInputStream ?
				positionedInputStream :
				new UncheckedPositionedInputStream(in));
	}

	public BytecodeInputStream(UncheckedPositionedInputStream in) {
		super(in);
		this.positioned = in;
	}


	public long getPosition() {
		return positioned.getPosition();
	}

	public int getIntPosition() {
		long position = positioned.getPosition();

		if ((int)position == position) {
			return (int)position;
		}

		throw new ArithmeticException("long overflow: " + position);
	}

	public BinaryInstruction readInstruction(BytecodeDisassemblingContext context) {
		byte opcode = readByte();

		return switch (opcode & 0xFF) {
			case NOP -> NopInstruction.INSTANCE;
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
			case LDC    -> new BinaryInstructionWithConstant<>("ldc",    ConstValueConstant.class, readUnsignedByte());
			case LDC_W  -> new BinaryInstructionWithConstant<>("ldc_w",  ConstValueConstant.class, readUnsignedShort());
			case LDC2_W -> new BinaryInstructionWithConstant<>("ldc2_w", ConstValueConstant.class, readUnsignedShort());

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

			case LCMP  -> Instructions.LCMP;
			case FCMPL -> Instructions.FCMPL;
			case FCMPG -> Instructions.FCMPG;
			case DCMPL -> Instructions.DCMPL;
			case DCMPG -> Instructions.DCMPG;

			case IFEQ -> new BinaryInstructionWithLabel("ifeq", context, readShort());
			case IFNE -> new BinaryInstructionWithLabel("ifne", context, readShort());
			case IFLT -> new BinaryInstructionWithLabel("iflt", context, readShort());
			case IFGE -> new BinaryInstructionWithLabel("ifge", context, readShort());
			case IFGT -> new BinaryInstructionWithLabel("ifgt", context, readShort());
			case IFLE -> new BinaryInstructionWithLabel("ifle", context, readShort());
			case IF_ICMPEQ -> new BinaryInstructionWithLabel("if_icmpeq", context, readShort());
			case IF_ICMPNE -> new BinaryInstructionWithLabel("if_icmpne", context, readShort());
			case IF_ICMPLT -> new BinaryInstructionWithLabel("if_icmplt", context, readShort());
			case IF_ICMPGE -> new BinaryInstructionWithLabel("if_icmpge", context, readShort());
			case IF_ICMPGT -> new BinaryInstructionWithLabel("if_icmpgt", context, readShort());
			case IF_ICMPLE -> new BinaryInstructionWithLabel("if_icmple", context, readShort());
			case IF_ACMPEQ -> new BinaryInstructionWithLabel("if_acmpeq", context, readShort());
			case IF_ACMPNE -> new BinaryInstructionWithLabel("if_acmpne", context, readShort());
			case GOTO -> new BinaryInstructionWithLabel("goto", context, readShort());

			case JSR -> new BinaryInstructionWithLabel("jsr", context, readShort());
			case RET -> new BinaryInstructionWithSlot("ret", readUnsignedByte());

			case TABLESWITCH -> {
				alignBy4Bytes();

				int pos = context.currentPos();

				int defaultPos = readInt() + pos,
					low = readInt(),
					high = readInt();

				if (high < low)
					throw new InstructionFormatException("tableswitch: high < low (low = " + low + ", high = " + high + ")");

				Int2IntMap posTable = new Int2IntLinkedOpenHashMap(high - low);

				for (int value = low; value <= high; value++) {
					posTable.put(value, readInt() + pos);
				}

				yield new BinarySwitchInstruction("tableswitch", context, defaultPos, posTable);
			}

			case LOOKUPSWITCH -> {
				alignBy4Bytes();

				int pos = context.currentPos();

				int defaultPos = readInt() + pos;
				int cases = readInt();

				Int2IntMap posTable = new Int2IntLinkedOpenHashMap(cases);

				for (; cases != 0; cases--) {
					posTable.put(readInt(), readInt() + pos);
				}

				yield new BinarySwitchInstruction("lookupswitch", context, defaultPos, posTable);
			}

			case IRETURN -> Instructions.IRETURN;
			case LRETURN -> Instructions.LRETURN;
			case FRETURN -> Instructions.FRETURN;
			case DRETURN -> Instructions.DRETURN;
			case ARETURN -> Instructions.ARETURN;
			case RETURN  -> Instructions.VRETURN;

			case GETSTATIC -> new BinaryInstructionWithConstant<>("getstatic", FieldrefConstant.class, readUnsignedShort());
			case PUTSTATIC -> new BinaryInstructionWithConstant<>("putstatic", FieldrefConstant.class, readUnsignedShort());
			case GETFIELD  -> new BinaryInstructionWithConstant<>("getfield",  FieldrefConstant.class, readUnsignedShort());
			case PUTFIELD  -> new BinaryInstructionWithConstant<>("putfield",  FieldrefConstant.class, readUnsignedShort());

			case INVOKEVIRTUAL   -> new BinaryInstructionWithConstant<>("invokevirtual", MethodrefConstant.class, readUnsignedShort());
			case INVOKESPECIAL   -> new BinaryInstructionWithConstant<>("invokespecial", MethodrefConstant.class, readUnsignedShort());
			case INVOKESTATIC    -> new BinaryInstructionWithConstant<>("invokestatic",  MethodrefConstant.class, readUnsignedShort());
			case INVOKEINTERFACE -> BinaryInstructionWithConstant.invokeInterface(readUnsignedShort(), readUnsignedByte(), readUnsignedByte());
			case INVOKEDYNAMIC   -> BinaryInstructionWithConstant.invokeDynamic(readUnsignedShort(), readUnsignedShort());

			case NEW         -> new BinaryInstructionWithConstant<>("new", ClassConstant.class, readUnsignedShort());
			case NEWARRAY    -> new BinaryNewArrayInstruction("newarray", readUnsignedByte());
			case ANEWARRAY   -> new BinaryInstructionWithConstant<>("anewarray", ClassConstant.class, readUnsignedShort());
			case ARRAYLENGTH -> Instructions.ARRAYLENGTH;

			case ATHROW     -> Instructions.ATHROW;
			case CHECKCAST  -> new BinaryInstructionWithConstant<>("checkcast",  ClassConstant.class, readUnsignedShort());
			case INSTANCEOF -> new BinaryInstructionWithConstant<>("instanceof", ClassConstant.class, readUnsignedShort());

			case MONITORENTER -> Instructions.MONITORENTER;
			case MONITOREXIT  -> Instructions.MONITOREXIT;

			case WIDE -> {
				int wideOpcode = readUnsignedByte();
				yield switch (wideOpcode) {
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
					case RET -> new BinaryInstructionWithSlot("ret", readUnsignedShort());
					default -> throw new InvalidOpcodeException("Illegal wide opcode 0x" + IntegerUtil.hex2(wideOpcode));
				};
			}

//			case MULTIANEWARRAY -> new MultiANewArrayInstruction(readUnsignedShort(), readUnsignedByte());

			case IFNULL    -> new BinaryInstructionWithLabel("ifnull",    context, readShort());
			case IFNONNULL -> new BinaryInstructionWithLabel("ifnonnull", context, readShort());
			case GOTO_W    -> new BinaryInstructionWithLabel("goto_w", context, readInt());
			case JSR_W     -> new BinaryInstructionWithLabel("jsr",    context, readInt());
			default -> throw new InvalidOpcodeException(IntegerUtil.hex2WithPrefix(opcode));
		};
	}

	private void alignBy4Bytes() {
		long skip = 4 - (getPosition() & 0x3);
		long skipped = skip(skip);
		if (skip != skipped)
			throw new IllegalStateException("skip = " + skip + ", skipped = " + skipped);

		if ((getPosition() & 0x3) != 0)
			throw new IllegalStateException("pos " + getPosition() + " is not aligned");
	}
}
