package x590.yava.attribute.code;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import x590.util.IntegerUtil;
import x590.yava.attribute.Attribute;
import x590.yava.attribute.Attributes;
import x590.yava.attribute.Sizes;
import x590.yava.clazz.ClassInfo;
import x590.yava.io.*;
import x590.yava.attribute.Attributes.Location;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.parsing.BytecodeParseException;
import x590.yava.exception.parsing.ParseException;
import x590.yava.type.primitive.PrimitiveType;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static x590.yava.Keywords.BOOLEAN;
import static x590.yava.Keywords.CHAR;
import static x590.yava.Keywords.FLOAT;
import static x590.yava.Keywords.DOUBLE;
import static x590.yava.Keywords.BYTE;
import static x590.yava.Keywords.SHORT;
import static x590.yava.Keywords.INT;
import static x590.yava.Keywords.LONG;
import static x590.yava.Keywords.CASE;
import static x590.yava.Keywords.DEFAULT;
import static x590.yava.context.Opcodes.*;

public class CodeAttribute extends Attribute {

	private final int maxStackSize, maxLocalsCount;
	private final byte[] code;
	private final ExceptionTable exceptionTable;
	private final Attributes attributes;


	private static final long NONE_POS = Long.MAX_VALUE;
	private static final long TIME_COST_MULTIPLIER = 3;

	public CodeAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);

		this.maxStackSize = in.readUnsignedShort();
		this.maxLocalsCount = in.readUnsignedShort();

		this.code = new byte[in.readInt()];
		in.readFully(code);

		this.exceptionTable = new ExceptionTable(in, pool);
		this.attributes = Attributes.read(in, pool, Location.CODE_ATTRIBUTE);
	}

	public CodeAttribute(String name, AssemblingInputStream in, ConstantPool pool) {
		super(name);

		in.requireNext('{');

		this.maxStackSize = in.requireNext("maxStackSize").requireNext('=').nextInt();
		in.requireNext(';');
		this.maxLocalsCount = in.requireNext("maxLocalsCount").requireNext('=').nextInt();
		in.requireNext(';');

		Attributes attributes = null;

		ExceptionTable exceptionTable = null;

		var byteArrayOut = new ByteArrayOutputStream();
		var out = new BytecodeOutputStream(byteArrayOut);

		var labelsManager = new LabelsManager();

		while (!in.advanceIfHasNext('}')) {
			String instruction = in.nextString();

			int opcode = switch (instruction) {
				case "nop" -> NOP;
				case "aconst_null" -> ACONST_NULL;
				case "iconst_m1" -> ICONST_M1;
				case "iconst_0"  -> ICONST_0;
				case "iconst_1"  -> ICONST_1;
				case "iconst_2"  -> ICONST_2;
				case "iconst_3"  -> ICONST_3;
				case "iconst_4"  -> ICONST_4;
				case "iconst_5"  -> ICONST_5;
				case "lconst_0"  -> LCONST_0;
				case "lconst_1"  -> LCONST_1;
				case "fconst_0"  -> FCONST_0;
				case "fconst_1"  -> FCONST_1;
				case "fconst_2"  -> FCONST_2;
				case "dconst_0"  -> DCONST_0;
				case "dconst_1"  -> DCONST_1;
				case "iload_0" -> ILOAD_0;
				case "iload_1" -> ILOAD_1;
				case "iload_2" -> ILOAD_2;
				case "iload_3" -> ILOAD_3;
				case "lload_0" -> LLOAD_0;
				case "lload_1" -> LLOAD_1;
				case "lload_2" -> LLOAD_2;
				case "lload_3" -> LLOAD_3;
				case "fload_0" -> FLOAD_0;
				case "fload_1" -> FLOAD_1;
				case "fload_2" -> FLOAD_2;
				case "fload_3" -> FLOAD_3;
				case "dload_0" -> DLOAD_0;
				case "dload_1" -> DLOAD_1;
				case "dload_2" -> DLOAD_2;
				case "dload_3" -> DLOAD_3;
				case "aload_0" -> ALOAD_0;
				case "aload_1" -> ALOAD_1;
				case "aload_2" -> ALOAD_2;
				case "aload_3" -> ALOAD_3;

				case "iaload" -> IALOAD;
				case "laload" -> LALOAD;
				case "faload" -> FALOAD;
				case "daload" -> DALOAD;
				case "aaload" -> AALOAD;
				case "baload" -> BALOAD;
				case "caload" -> CALOAD;
				case "saload" -> SALOAD;

				case "istore_0" -> ISTORE_0;
				case "istore_1" -> ISTORE_1;
				case "istore_2" -> ISTORE_2;
				case "istore_3" -> ISTORE_3;
				case "lstore_0" -> LSTORE_0;
				case "lstore_1" -> LSTORE_1;
				case "lstore_2" -> LSTORE_2;
				case "lstore_3" -> LSTORE_3;
				case "fstore_0" -> FSTORE_0;
				case "fstore_1" -> FSTORE_1;
				case "fstore_2" -> FSTORE_2;
				case "fstore_3" -> FSTORE_3;
				case "dstore_0" -> DSTORE_0;
				case "dstore_1" -> DSTORE_1;
				case "dstore_2" -> DSTORE_2;
				case "dstore_3" -> DSTORE_3;
				case "astore_0" -> ASTORE_0;
				case "astore_1" -> ASTORE_1;
				case "astore_2" -> ASTORE_2;
				case "astore_3" -> ASTORE_3;

				case "iastore" -> IASTORE;
				case "lastore" -> LASTORE;
				case "fastore" -> FASTORE;
				case "dastore" -> DASTORE;
				case "aastore" -> AASTORE;
				case "bastore" -> BASTORE;
				case "castore" -> CASTORE;
				case "sastore" -> SASTORE;

				case "pop"     -> POP;
				case "pop2"    -> POP2;
				case "dup"     -> DUP;
				case "dup_x1"  -> DUP_X1;
				case "dup_x2"  -> DUP_X2;
				case "dup2"    -> DUP2;
				case "dup2_x1" -> DUP2_X1;
				case "dup2_x2" -> DUP2_X2;
				case "swap"    -> SWAP;

				case "iadd" -> IADD;
				case "ladd" -> LADD;
				case "fadd" -> FADD;
				case "dadd" -> DADD;
				case "isub" -> ISUB;
				case "lsub" -> LSUB;
				case "fsub" -> FSUB;
				case "dsub" -> DSUB;
				case "imul" -> IMUL;
				case "lmul" -> LMUL;
				case "fmul" -> FMUL;
				case "dmul" -> DMUL;
				case "idiv" -> IDIV;
				case "ldiv" -> LDIV;
				case "fdiv" -> FDIV;
				case "ddiv" -> DDIV;
				case "irem" -> IREM;
				case "lrem" -> LREM;
				case "frem" -> FREM;
				case "drem" -> DREM;
				case "ineg" -> INEG;
				case "lneg" -> LNEG;
				case "fneg" -> FNEG;
				case "dneg" -> DNEG;

				case "ishl"  -> ISHL;
				case "lshl"  -> LSHL;
				case "ishr"  -> ISHR;
				case "lshr"  -> LSHR;
				case "iushr" -> IUSHR;
				case "lushr" -> LUSHR;
				case "iand"  -> IAND;
				case "land"  -> LAND;
				case "ior"   -> IOR;
				case "lor"   -> LOR;
				case "ixor"  -> IXOR;
				case "lxor"  -> LXOR;

				case "i2l" -> I2L;
				case "i2f" -> I2F;
				case "i2d" -> I2D;
				case "l2i" -> L2I;
				case "l2f" -> L2F;
				case "l2d" -> L2D;
				case "f2i" -> F2I;
				case "f2l" -> F2L;
				case "f2d" -> F2D;
				case "d2i" -> D2I;
				case "d2l" -> D2L;
				case "d2f" -> D2F;
				case "i2b" -> I2B;
				case "i2c" -> I2C;
				case "i2s" -> I2S;

				case "lcmp"  -> LCMP;
				case "fcmpl" -> FCMPL;
				case "fcmpg" -> FCMPG;
				case "dcmpl" -> DCMPL;
				case "dcmpg" -> DCMPG;

				case "ireturn" -> IRETURN;
				case "lreturn" -> LRETURN;
				case "freturn" -> FRETURN;
				case "dreturn" -> DRETURN;
				case "areturn" -> ARETURN;
				case "return"  -> RETURN;

				case "arraylength" -> ARRAYLENGTH;
				case "athrow"      -> ATHROW;

				case "monitorenter" -> MONITORENTER;
				case "monitorexit"  -> MONITOREXIT;

				default -> -1;
			};

			if (opcode != -1) {
				out.write(opcode);
			} else {

				switch (instruction) {

					case "iconst" -> {
						int value = in.nextInt();
						switch (value) {
							case -1 -> out.write(ICONST_M1);
							case  0 -> out.write(ICONST_0);
							case  1 -> out.write(ICONST_1);
							case  2 -> out.write(ICONST_2);
							case  3 -> out.write(ICONST_3);
							case  4 -> out.write(ICONST_4);
							case  5 -> out.write(ICONST_5);
							default -> {

								if ((byte)value == value) {
									out.writeOpcodeWithByte(value, BIPUSH);

								} else if ((short)value == value) {
									out.writeOpcodeWithShort(value, SIPUSH);

								} else {
									out.writeLdcOrLdcW(pool.findOrAddInteger(value));
								}
							}
						}
					}

					case "lconst" -> {
						long value = in.nextLong();

						if(value == 0) {
							out.write(LCONST_0);
						} else if(value == 1) {
							out.write(LCONST_1);
						} else {
							out.writeOpcodeWithShort(pool.findOrAddLong(value), LDC2_W);
						}
					}

					case "fconst" -> {
						float value = in.nextFloat();

						if(value == 0) {
							out.write(FCONST_0);
						} else if(value == 1) {
							out.write(FCONST_1);
						} else if(value == 2) {
							out.write(FCONST_2);
						} else {
							out.writeLdcOrLdcW(pool.findOrAddFloat(value));
						}
					}

					case "dconst" -> {
						double value = in.nextDouble();

						if(value == 0) {
							out.write(DCONST_0);
						} else if(value == 1) {
							out.write(DCONST_1);
						} else {
							out.writeOpcodeWithShort(pool.findOrAddDouble(value), LDC2_W);
						}
					}

					case "bipush" -> out.writeOpcodeWithByte(in.nextShort(),  BIPUSH);
					case "sipush" -> out.writeOpcodeWithShort(in.nextShort(), SIPUSH);

					case "ldc", "ldc_w", "ldc2_w" -> {
						int index = in.nextLiteralConstant(pool);
						if ((short)index != index) {
							throw BytecodeParseException.tooLargeValue(index, "constant pool index", instruction);
						}

						if (pool.get(index).holdsTwo()) {
							out.writeOpcodeWithShort(index, LDC2_W);
						} else {
							out.writeLdcOrLdcW(index);
						}
					}

					case "iload" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), ILOAD);
					case "lload" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), LLOAD);
					case "fload" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), FLOAD);
					case "dload" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), DLOAD);
					case "aload" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), ALOAD);

					case "istore" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), ISTORE);
					case "lstore" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), LSTORE);
					case "fstore" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), FSTORE);
					case "dstore" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), DSTORE);
					case "astore" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), ASTORE);

					case "iinc" ->
						out .recordByte(IINC)
							.recordByte(in.nextUnsignedByte())
							.recordByte(in.nextByte());

					case "ifeq" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IFEQ);
					case "ifne" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IFNE);
					case "iflt" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IFLT);
					case "ifge" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IFGE);
					case "ifgt" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IFGT);
					case "ifle" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IFLE);
					case "if_icmpeq" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IF_ICMPEQ);
					case "if_icmpne" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IF_ICMPNE);
					case "if_icmplt" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IF_ICMPLT);
					case "if_icmpge" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IF_ICMPGE);
					case "if_icmpgt" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IF_ICMPGT);
					case "if_icmple" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IF_ICMPLE);
					case "if_acmpeq" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IF_ACMPEQ);
					case "if_acmpne" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IF_ACMPNE);
					case "ifnull"    -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IFNULL);
					case "ifnonnull" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), IFNONNULL);

					case "goto" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), GOTO);
					case "goto_w" -> out.writeOpcodeWithInt(in.nextLabel(labelsManager.intPosFrom(out)), GOTO_W);

					case "jsr" -> out.writeOpcodeWithShort(in.nextLabel(labelsManager.shortPosFrom(out)), JSR);
					case "jsr_w" -> out.writeOpcodeWithInt(in.nextLabel(labelsManager.intPosFrom(out)), JSR_W);

					case "ret" -> out.writeOpcodeWithByte(in.nextUnsignedByte(), RET);

					case "switch", "tableswitch", "lookupswitch" -> {
						in.requireNext('{');

						Int2IntMap table = new Int2IntArrayMap();
						long longDefaultPos = NONE_POS;

						while (!in.advanceIfHasNext('}')) {
							String str = in.nextString();

							switch (str) {
								case CASE -> {
									int index = in.nextInt(),
										label = in.requireNext(':').nextLabel(labelsManager.shortPosFrom(out));

									if (table.containsKey(index)) {
										throw new ParseException("duplicated \"case " + index + "\" statement");
									}

									table.put(index, label);
								}

								case DEFAULT -> {
									if (longDefaultPos != NONE_POS) {
										throw new ParseException("duplicated \"default\" statement");
									}

									longDefaultPos = in.requireNext(':').nextLabel(labelsManager);
								}

								default -> throw ParseException.expectedButGot("\"case\" or \"default\"", str);
							}
						}

						if (longDefaultPos == NONE_POS) {
							throw new ParseException("\"default\" statement is not specified");
						}


						int low = table.keySet().intStream().min().orElse(0),
							high = table.keySet().intStream().max().orElse(0);

						int size = table.size();


						// Если кому интересно, скопировано отсюда: https://habr.com/ru/articles/174065/
						// Размеры инструкции в int-ах
						long tableSpaceCost = ((long) high - low + 1) + 4,
							 lookupSpaceCost = 3 + size * 2L;

						int switchOpcode = (size > 0 &&
								tableSpaceCost + 3 /* Количество сравнений */ * TIME_COST_MULTIPLIER <=
								lookupSpaceCost + size * TIME_COST_MULTIPLIER) ?
								TABLESWITCH : LOOKUPSWITCH;


						out.write(switchOpcode);

						int padding = (4 - out.size()) & 0x3;
						for (int i = 0; i < padding; i++) {
							out.write(0);
						}

						int defaultPos = (int)longDefaultPos;
						out.writeInt(defaultPos);


						if (switchOpcode == TABLESWITCH) {
							out.writeInt(low);
							out.writeInt(high);

							for (int i = low; i < high; i++) {
								out.writeInt(table.getOrDefault(i, defaultPos));
							}

						} else {
							out.writeInt(size);

							table.int2IntEntrySet().stream()
								.sorted(Comparator.comparingInt(Int2IntMap.Entry::getIntKey))
								.forEach(entry -> {
									out.writeInt(entry.getIntKey());
									out.writeInt(entry.getIntValue());
								});
						}
					}

					case "getstatic" -> out.writeOpcodeWithShort(in.nextFieldref(pool), GETSTATIC);
					case "putstatic" -> out.writeOpcodeWithShort(in.nextFieldref(pool), PUTSTATIC);
					case "getfield"  -> out.writeOpcodeWithShort(in.nextFieldref(pool), GETFIELD);
					case "putfield"  -> out.writeOpcodeWithShort(in.nextFieldref(pool), PUTFIELD);

					case "invokevirtual" -> out.writeOpcodeWithShort(in.nextMethodref(pool), INVOKEVIRTUAL);
					case "invokespecial" -> out.writeOpcodeWithShort(in.nextMethodref(pool), INVOKESPECIAL);
					case "invokestatic"  -> out.writeOpcodeWithShort(in.nextMethodref(pool), INVOKESTATIC);

					case "invokeinterface" -> {
						out.writeOpcodeWithShort(in.nextMethodref(pool), INVOKEINTERFACE);
						out.write(0x01);
						out.write(0x00);
					}

					case "invokedynamic" -> {
						out.writeOpcodeWithShort(in.nextMethodref(pool), INVOKEDYNAMIC);
						out.write(0x00);
						out.write(0x00);
					}

					case "new" -> out.writeOpcodeWithShort(pool.classIndexFor(in.nextClassType()), NEW);

					case "newarray" -> {
						PrimitiveType type = in.nextPrimitiveType();

						int code = switch (type.getName()) {
							case BOOLEAN -> 0x4;
							case CHAR    -> 0x5;
							case FLOAT   -> 0x6;
							case DOUBLE  -> 0x7;
							case BYTE    -> 0x8;
							case SHORT   -> 0x9;
							case INT     -> 0xA;
							case LONG    -> 0xB;
							default      ->
									throw new ParseException("Invalid type for newarray instruction: " + type);
						};

						out.writeOpcodeWithByte(code, NEWARRAY);
					}

					case "anewarray"  -> out.writeOpcodeWithShort(pool.classIndexFor(in.nextReferenceType()), ANEWARRAY);

					case "multianewarray" -> {
						out.writeOpcodeWithShort(pool.classIndexFor(in.nextArrayType()), MULTIANEWARRAY);
						out.write(in.nextUnsignedByte());
					}

					case "checkcast"  -> out.writeOpcodeWithShort(pool.classIndexFor(in.nextReferenceType()), CHECKCAST);
					case "instanceof" -> out.writeOpcodeWithShort(pool.classIndexFor(in.nextReferenceType()), INSTANCEOF);

					case "wide" -> {
						String wideInstruction = in.nextString();

						out.write(WIDE);

						switch (wideInstruction) {
							case "iload" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), ILOAD);
							case "lload" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), LLOAD);
							case "fload" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), FLOAD);
							case "dload" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), DLOAD);
							case "aload" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), ALOAD);
							case "istore" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), ISTORE);
							case "lstore" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), LSTORE);
							case "fstore" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), FSTORE);
							case "dstore" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), DSTORE);
							case "astore" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), ASTORE);
							case "ret" -> out.writeOpcodeWithShort(in.nextUnsignedShort(), RET);
						}
					}

					case "ExceptionTable" -> {
						if (exceptionTable != null) {
							throw new ParseException("duplicated \"ExceptionTable\" tag");
						}

						exceptionTable = new ExceptionTable(in, labelsManager);
					}

					case "attributes" -> {
						if (attributes != null) {
							throw new ParseException("duplicated \"attributes\" tag");
						}

						attributes = Attributes.parse(in, pool, Location.CODE_ATTRIBUTE);
					}

					default -> {
						if (in.advanceIfHasNext(':')) {
							labelsManager.addLabel(instruction, out.size());
						} else {
							throw new BytecodeParseException("invalid instruction \"" + instruction + "\"");
						}
					}
				}
			}
		}

		this.code = byteArrayOut.toByteArray();

		labelsManager.resolveLabels(code);

		this.exceptionTable = exceptionTable = exceptionTable == null ? ExceptionTable.EMPTY_TABLE : exceptionTable;

		attributes = attributes == null ? Attributes.empty() : attributes;

		this.attributes = attributes;

		initLength(
				Sizes.SHORT + Sizes.SHORT + // maxStackSize, maxLocalsCount
				Sizes.INT + code.length +  // code length, code
				exceptionTable.getFullLength() +
				attributes.getFullLength()
		);
	}


	
	public static final class LabelsManager {

		// key: label name, value: pos
		private final Object2IntMap<String> labelsTable = new Object2IntArrayMap<>();
		private final List<ForwardingLabelEntry> forwardingLabels = new ArrayList<>();
		
		private int instructionPos, replacePos = -1, size;

		public void addLabel(String name, int pos) {
			if (name.isEmpty() || !Character.isJavaIdentifierStart(name.charAt(0))) {
				throw new ParseException("invalid label name \"" + name + "\"");
			}

			if (labelsTable.containsKey(name)) {
				throw new ParseException("Label \"" + name + "\" already declared");
			}

			labelsTable.put(name, pos);
		}

		public boolean hasLabel(String name) {
			return labelsTable.containsKey(name);
		}

		public int getLabelPos(String name) {
			return labelsTable.getInt(name);
		}

		public void addForwardingEntry(String name) {
			if(replacePos == -1) {
				throw new ParseException("Cannot find label \"" + name + "\"");
			}

			forwardingLabels.add(new ForwardingLabelEntry(name, instructionPos, replacePos, size));
		}
		
		public LabelsManager shortPosFrom(BytecodeOutputStream out) {
			return posFrom(out, Sizes.SHORT);
		}

		public LabelsManager intPosFrom(BytecodeOutputStream out) {
			return posFrom(out, Sizes.BYTE);
		}

		public LabelsManager posFrom(BytecodeOutputStream out, int size) {
			this.instructionPos = out.size();
			this.replacePos = instructionPos + 1;
			this.size = size;
			return this;
		}

		public void resetPos() {
			this.instructionPos = 0;
			this.replacePos = -1;
		}

		public int getInstructionPos() {
			return instructionPos;
		}

		public void resolveLabels(byte[] code) {
			for (var forwardingLabel : forwardingLabels) {
				String name = forwardingLabel.name;

				if (!labelsTable.containsKey(name)) {
					throw new ParseException("Cannot find label \"" + name + "\"");
				}

				int offset = labelsTable.getInt(name) - forwardingLabel.instructionPos;

				for (int i = forwardingLabel.size, j = forwardingLabel.replacePos; i > 0; j++) {
					assert code[j] == 0 : IntegerUtil.hexWithPrefix(code[j] & 0xFF);
					code[j] = (byte)(offset >>> --i * 8);
				}
			}
		}

		public record ForwardingLabelEntry(String name, int instructionPos, int replacePos, int size) {}
	}
	


	CodeAttribute(String name, int length, int maxStack, int maxLocals, byte[] code, ExceptionTable exceptionTable, Attributes attributes) {
		super(name, length);

		this.maxStackSize = maxStack;
		this.maxLocalsCount = maxLocals;
		this.code = code;
		this.exceptionTable = exceptionTable;
		this.attributes = attributes;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public int getMaxLocalsCount() {
		return maxLocalsCount;
	}

	public byte[] getCode() {
		return code;
	}

	public ExceptionTable getExceptionTable() {
		return exceptionTable;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public static EmptyCodeAttribute empty() {
		return EmptyCodeAttribute.INSTANCE;
	}

	public boolean isEmpty() {
		return false;
	}


	@Override
	protected void writeDisassembledContent(DisassemblingOutputStream out, ClassInfo classinfo) {
		out .printIndent().print("maxStackSize = ").printInt(maxStackSize).println(';')
			.printIndent().print("maxLocalsCount = ").printInt(maxLocalsCount).println(';');

		BytecodeDisassemblingContext.disassemble(classinfo, code).writeDisassembled(out);

//		if (!attributes.isEmpty()) {
//			out.println().printIndent().print("attributes").print(attributes, classinfo);
//		}
	}


	@Override
	public void serializeData(AssemblingOutputStream out, ConstantPool pool) {
		out .recordShort(maxStackSize).recordShort(maxLocalsCount)
			.recordInt(code.length).recordByteArray(code)
			.record(exceptionTable, pool)
			.record(attributes, pool);
	}
}
