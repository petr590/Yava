package x590.yava.attribute;

import x590.util.LoopUtil;
import x590.util.Util;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.JavaSerializable;
import x590.yava.attribute.Attributes.Location;
import x590.yava.constpool.ConstantPool;
import x590.yava.context.Context;
import x590.yava.context.DecompilationContext;
import x590.yava.exception.parsing.BytecodeParseException;
import x590.yava.exception.parsing.ParseException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;
import x590.yava.scope.CatchScope;
import x590.yava.scope.FinallyScope;
import x590.yava.scope.Scope;
import x590.yava.scope.TryScope;
import x590.yava.type.reference.ClassType;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeAttribute extends Attribute {

	private final int maxStackSize, maxLocalsCount;
	private final byte[] code;
	private final ExceptionTable exceptionTable;
	private final Attributes attributes;

	CodeAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);

		this.maxStackSize = in.readUnsignedShort();
		this.maxLocalsCount = in.readUnsignedShort();

		this.code = new byte[in.readInt()];
		in.readFully(code);

		this.exceptionTable = new ExceptionTable(in, pool);
		this.attributes = Attributes.read(in, pool, Location.CODE_ATTRIBUTE);
	}

	CodeAttribute(String name, AssemblingInputStream in, ConstantPool pool) {
		super(name);

		in.requireNext('{');

		this.maxStackSize = in.requireNext("maxStackSize").requireNext('=').nextInt();
		in.requireNext(';');
		this.maxLocalsCount = in.requireNext("maxLocalsCount").requireNext('=').nextInt();
		in.requireNext(';');

		Attributes attributes = null;

		var out = new ByteArrayOutputStream();

		while (!in.advanceIfHasNext('}')) {
			String instruction = in.nextString();

			int opcode = switch (instruction) {
				case "nop" -> 0x00;
				case "aconst_null" -> 0x01;
				case "iconst_m1" -> 0x02;
				case "iconst_0" -> 0x03;
				case "iconst_1" -> 0x04;
				case "iconst_2" -> 0x05;
				case "iconst_3" -> 0x06;
				case "iconst_4" -> 0x07;
				case "iconst_5" -> 0x08;
				case "lconst_0" -> 0x09;
				case "lconst_1" -> 0x0A;
				case "fconst_0" -> 0x0B;
				case "fconst_1" -> 0x0C;
				case "fconst_2" -> 0x0D;
				case "dconst_0" -> 0x0E;
				case "dconst_1" -> 0x0F;
				case "iload_0" -> 0x1A;
				case "iload_1" -> 0x1B;
				case "iload_2" -> 0x1C;
				case "iload_3" -> 0x1D;
				case "lload_0" -> 0x1E;
				case "lload_1" -> 0x1F;
				case "lload_2" -> 0x20;
				case "lload_3" -> 0x21;
				case "fload_0" -> 0x22;
				case "fload_1" -> 0x23;
				case "fload_2" -> 0x24;
				case "fload_3" -> 0x25;
				case "dload_0" -> 0x26;
				case "dload_1" -> 0x27;
				case "dload_2" -> 0x28;
				case "dload_3" -> 0x29;
				case "aload_0" -> 0x2A;
				case "aload_1" -> 0x2B;
				case "aload_2" -> 0x2C;
				case "aload_3" -> 0x2D;

				case "iaload" -> 0x2E;
				case "laload" -> 0x2F;
				case "faload" -> 0x30;
				case "daload" -> 0x31;
				case "aaload" -> 0x32;
				case "baload" -> 0x33;
				case "caload" -> 0x34;
				case "saload" -> 0x35;

				case "istore_0" -> 0x3B;
				case "istore_1" -> 0x3C;
				case "istore_2" -> 0x3D;
				case "istore_3" -> 0x3E;
				case "lstore_0" -> 0x3F;
				case "lstore_1" -> 0x40;
				case "lstore_2" -> 0x41;
				case "lstore_3" -> 0x42;
				case "fstore_0" -> 0x43;
				case "fstore_1" -> 0x44;
				case "fstore_2" -> 0x45;
				case "fstore_3" -> 0x46;
				case "dstore_0" -> 0x47;
				case "dstore_1" -> 0x48;
				case "dstore_2" -> 0x49;
				case "dstore_3" -> 0x4A;
				case "astore_0" -> 0x4B;
				case "astore_1" -> 0x4C;
				case "astore_2" -> 0x4D;
				case "astore_3" -> 0x4E;

				case "iastore" -> 0x4F;
				case "lastore" -> 0x50;
				case "fastore" -> 0x51;
				case "dastore" -> 0x52;
				case "aastore" -> 0x53;
				case "bastore" -> 0x54;
				case "castore" -> 0x55;
				case "sastore" -> 0x56;

				case "pop" -> 0x57;
				case "pop2" -> 0x58;
				case "dup" -> 0x59;
				case "dup_x1" -> 0x5A;
				case "dup_x2" -> 0x5B;
				case "dup2" -> 0x5C;
				case "dup2_x1" -> 0x5D;
				case "dup2_x2" -> 0x5E;
				case "swap" -> 0x5F;

				case "iadd" -> 0x60;
				case "ladd" -> 0x61;
				case "fadd" -> 0x62;
				case "dadd" -> 0x63;
				case "isub" -> 0x64;
				case "lsub" -> 0x65;
				case "fsub" -> 0x66;
				case "dsub" -> 0x67;
				case "imul" -> 0x68;
				case "lmul" -> 0x69;
				case "fmul" -> 0x6A;
				case "dmul" -> 0x6B;
				case "idiv" -> 0x6C;
				case "ldiv" -> 0x6D;
				case "fdiv" -> 0x6E;
				case "ddiv" -> 0x6F;
				case "irem" -> 0x70;
				case "lrem" -> 0x71;
				case "frem" -> 0x72;
				case "drem" -> 0x73;
				case "ineg" -> 0x74;
				case "lneg" -> 0x75;
				case "fneg" -> 0x76;
				case "dneg" -> 0x77;

				case "ishl" -> 0x78;
				case "lshl" -> 0x79;
				case "ishr" -> 0x7A;
				case "lshr" -> 0x7B;
				case "iushr" -> 0x7C;
				case "lushr" -> 0x7D;
				case "iand" -> 0x7E;
				case "land" -> 0x7F;
				case "ior" -> 0x80;
				case "lor" -> 0x81;
				case "ixor" -> 0x82;
				case "lxor" -> 0x83;

				case "i2l" -> 0x85;
				case "i2f" -> 0x86;
				case "i2d" -> 0x87;
				case "l2i" -> 0x88;
				case "l2f" -> 0x89;
				case "l2d" -> 0x8A;
				case "f2i" -> 0x8B;
				case "f2l" -> 0x8C;
				case "f2d" -> 0x8D;
				case "d2i" -> 0x8E;
				case "d2l" -> 0x8F;
				case "d2f" -> 0x90;
				case "i2b" -> 0x91;
				case "i2c" -> 0x92;
				case "i2s" -> 0x93;

				case "lcmp" -> 0x94;
				case "fcmpl" -> 0x95;
				case "fcmpg" -> 0x96;
				case "dcmpl" -> 0x97;
				case "dcmpg" -> 0x98;

				case "ireturn" -> 0xAC;
				case "lreturn" -> 0xAD;
				case "freturn" -> 0xAE;
				case "dreturn" -> 0xAF;
				case "areturn" -> 0xB0;
				case "return" -> 0xB1;

				case "arraylength" -> 0xBE;
				case "athrow" -> 0xBF;

				case "monitorenter" -> 0xC2;
				case "monitorexit" -> 0xC3;

				default -> -1;
			};

			if (opcode != -1) {
				out.write(opcode);
			} else {
				switch (instruction) {
					case "bipush" -> readShortAndWrite(in, out, "integer constant", instruction, 0x10);
					case "sipush" -> readShortAndWrite(in, out, "integer constant", instruction, 0x11);

					case "ldc", "ldc_w", "ldc2_w" -> {
						int index = in.nextConstant(pool);
						if ((short) index != index) {
							throw BytecodeParseException.tooLargeValue(index, "constant pool index", instruction);
						}

						if (pool.get(index).holdsTwo()) { // ldc2_w
							writeShort(out, index, 0x14);

						} else if ((byte) index != index) { // ldc_w
							writeShort(out, index, 0x13);

						} else { // ldc
							writeByte(out, index, 0x12);
						}
					}

					case "iload" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x15);
					case "lload" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x16);
					case "fload" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x17);
					case "dload" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x18);
					case "aload" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x19);

					case "istore" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x36);
					case "lstore" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x37);
					case "fstore" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x38);
					case "dstore" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x39);
					case "astore" -> readUnsignedByteAndWrite(in, out, "variable slot", instruction, 0x3A);

					case "iinc" -> {
						int index = readUnsignedByte(in, "variable slot", instruction);
						int value = readByte(in, "integer constant", instruction);
						out.write(0x84);
						out.write(index);
						out.write(value);
					}

					case "ifeq" -> readShortAndWrite(in, out, "offset", instruction, 0x99);
					case "ifne" -> readShortAndWrite(in, out, "offset", instruction, 0x9A);
					case "iflt" -> readShortAndWrite(in, out, "offset", instruction, 0x9B);
					case "ifge" -> readShortAndWrite(in, out, "offset", instruction, 0x9C);
					case "ifgt" -> readShortAndWrite(in, out, "offset", instruction, 0x9D);
					case "ifle" -> readShortAndWrite(in, out, "offset", instruction, 0x9E);
					case "if_icmpeq" -> readShortAndWrite(in, out, "offset", instruction, 0x9F);
					case "if_icmpne" -> readShortAndWrite(in, out, "offset", instruction, 0xA0);
					case "if_icmplt" -> readShortAndWrite(in, out, "offset", instruction, 0xA1);
					case "if_icmpge" -> readShortAndWrite(in, out, "offset", instruction, 0xA2);
					case "if_icmpgt" -> readShortAndWrite(in, out, "offset", instruction, 0xA3);
					case "if_icmple" -> readShortAndWrite(in, out, "offset", instruction, 0xA4);
					case "if_acmpeq" -> readShortAndWrite(in, out, "offset", instruction, 0xA5);
					case "if_acmpne" -> readShortAndWrite(in, out, "offset", instruction, 0xA6);
					case "goto" -> readShortAndWrite(in, out, "offset", instruction, 0xA7);
					case "jsr" -> readShortAndWrite(in, out, "offset", instruction, 0xA8);
					case "ret" -> readUnsignedByteAndWrite(in, out, "index", instruction, 0xA9);

//					case "tableswitch" -> {
//						// TODO
//					}
//
//					case "lookupswitch" -> {
//						// TODO
//					}

					case "getstatic" -> writeShort(out, in.nextFieldref(pool), 0xB2);
					case "putstatic" -> writeShort(out, in.nextFieldref(pool), 0xB3);
					case "getfield" -> writeShort(out, in.nextFieldref(pool), 0xB4);
					case "putfield" -> writeShort(out, in.nextFieldref(pool), 0xB5);

					case "invokevirtual" -> writeShort(out, in.nextMethodref(pool), 0xB6);
					case "invokespecial" -> writeShort(out, in.nextMethodref(pool), 0xB7);
					case "invokestatic" -> writeShort(out, in.nextMethodref(pool), 0xB8);

					case "invokeinterface" -> {
						writeShort(out, in.nextMethodref(pool), 0xB9);
						out.write(0x01);
						out.write(0x00);
					}

					case "invokedynamic" -> {
						writeShort(out, in.nextMethodref(pool), 0xBA);
						out.write(0x00);
						out.write(0x00);
					}

					case "attributes" -> {
						if (attributes != null) {
							throw new ParseException("duplicated \"attributes\" tag");
						}

						attributes = Attributes.parse(in, pool, Location.CODE_ATTRIBUTE);
					}

					default -> throw new BytecodeParseException("invalid instruction \"" + instruction + "\"");
				}
			}
		}

		this.code = out.toByteArray();

		this.exceptionTable = ExceptionTable.EMPTY_TABLE;

		attributes = attributes == null ? Attributes.empty() : attributes;

		this.attributes = attributes;

		initLength(
				Short.BYTES + Short.BYTES +   // maxStackSize, maxLocalsCount
						Integer.BYTES + code.length + // code
						Short.BYTES +                 // exception table
						attributes.getLength()        // attributes
		);
	}


	private static int readByte(AssemblingInputStream in, String valueName, String instruction) {
		int value = in.nextInt();

		if ((byte) value == value) {
			return value;
		}

		throw BytecodeParseException.tooLargeValue(value, valueName, instruction);
	}

	private static int readUnsignedByte(AssemblingInputStream in, String valueName, String instruction) {
		int value = in.nextUnsignedInt();

		if ((value & 0xFF) == value) {
			return value;
		}

		throw BytecodeParseException.tooLargeValue(value, valueName, instruction);
	}


	private static void readByteAndWrite(AssemblingInputStream in, ByteArrayOutputStream out, String valueName, String instruction, int opcode) {
		out.write(opcode);
		out.write(readByte(in, valueName, instruction));
	}

	private static void readUnsignedByteAndWrite(AssemblingInputStream in, ByteArrayOutputStream out, String valueName, String instruction, int opcode) {
		out.write(opcode);
		out.write(readUnsignedByte(in, valueName, instruction));
	}

	private static void readShortAndWrite(AssemblingInputStream in, ByteArrayOutputStream out, String valueName, String instruction, int opcode) {
		int value = in.nextInt();

		if ((short) value != value) {
			throw BytecodeParseException.tooLargeValue(value, valueName, instruction);
		}

		out.write(opcode);
		out.write(value);
		out.write(value >>> 8);
	}

	private static void writeByte(ByteArrayOutputStream out, int value, int opcode) {
		out.write(opcode);
		out.write(value);
	}

	private static void writeShort(ByteArrayOutputStream out, int value, int opcode) {
		out.write(opcode);
		out.write(value);
		out.write(value >>> 8);
	}


	protected CodeAttribute(String name, int length, int maxStack, int maxLocals, byte[] code, ExceptionTable exceptionTable, Attributes attributes) {
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


	public static class ExceptionTable implements JavaSerializable {

		private static final ExceptionTable EMPTY_TABLE = new ExceptionTable();

		private final @Immutable List<TryEntry> entries;

		private ExceptionTable() {
			this.entries = Collections.emptyList();
		}

		public ExceptionTable(ExtendedDataInputStream in, ConstantPool pool) {
			int size = in.readUnsignedShort();

			List<TryEntry> entries = new ArrayList<>(size);

			for (int i = 0; i < size; i++) {
				TryEntry.readTo(in, pool, entries);
			}

			Collections.sort(entries);
			entries.forEach(TryEntry::freeze);

			this.entries = Collections.unmodifiableList(entries);
		}

		public static ExceptionTable empty() {
			return EMPTY_TABLE;
		}

		public @Immutable List<TryEntry> getEntries() {
			return entries;
		}

		@Override
		public void serialize(ExtendedDataOutputStream out) {
			out.writeAll(entries);
		}


		public static class TryEntry implements Comparable<TryEntry>, JavaSerializable {
			private final int startPos, endPos;
			private @Immutable List<CatchEntry> catchEntries = new ArrayList<>();

			private TryEntry(int startPos, int endPos) {
				this.startPos = startPos;
				this.endPos = endPos;
			}

			public void addCatchEntry(@Nullable CatchEntry catchEntry) {
				if (catchEntry != null)
					catchEntries.add(catchEntry);
			}

			public int getStartPos() {
				return startPos;
			}

			public int getEndPos() {
				return endPos;
			}

			public int getStartIndex(Context context) {
				return context.posToIndex(startPos);
			}

			public int getEndIndex(Context context) {
				return context.posToIndex(endPos);
			}

			public int getFactualEndIndex(Context context) {
				return getEndIndex(context) - (isFinally() ? 1 : 0);
			}

			public @Immutable List<CatchEntry> getCatchEntries() {
				return catchEntries;
			}

			public boolean isFinally() {
				return catchEntries.stream().allMatch(CatchEntry::isFinally);
			}

			public void setLastPos(int lastCatchEntryEndPos) {
				catchEntries.get(catchEntries.size() - 1).setEndPos(lastCatchEntryEndPos);
			}

			private void freeze() {
				Collections.sort(catchEntries);

				LoopUtil.forEachPair(catchEntries, (entry1, entry2) -> {
					entry1.setEndPos(entry2.getStartPos());
					entry1.setHasNext();
				});

				catchEntries.forEach(CatchEntry::freeze);
				this.catchEntries = Collections.unmodifiableList(catchEntries);
			}

			private static void readTo(ExtendedDataInputStream in, ConstantPool pool, List<TryEntry> entries) {
				int startPos = in.readUnsignedShort(),
						endPos = in.readUnsignedShort();

				TryEntry tryEntry = entries.stream()
						.filter(entry -> entry.startPos == startPos && entry.endPos == endPos).findAny()
						.orElseGet(() -> Util.addAndGetBack(entries, new TryEntry(startPos, endPos)));

				CatchEntry.readTo(in, pool, tryEntry.catchEntries, entries);
			}

			public Scope createScope(DecompilationContext context) {
				return new TryScope(context, getFactualEndIndex(context) + 1);
			}

			@Override
			public int compareTo(TryEntry other) {
				int diff = other.startPos - startPos;
				if (diff != 0)
					return diff;

				return other.endPos - endPos;
			}

			@Override
			public void serialize(ExtendedDataOutputStream out) {
				catchEntries.forEach(entry -> {
					out.writeShort(startPos);
					out.writeShort(endPos);
					out.writeShort(entry.startPos);
					out.writeShort(entry.exceptionTypeIndex);
				});
			}
		}


		public static class CatchEntry implements Comparable<CatchEntry> {

			private static final int NPOS = -1;

			private final int startPos, exceptionTypeIndex;
			private @Immutable List<ClassType> exceptionTypes = new ArrayList<>();
			private int endPos = NPOS;
			private boolean hasNext;

			private void addExceptionType(ConstantPool pool, int exceptionTypeIndex) {
				if (exceptionTypeIndex != 0) {
					exceptionTypes.add(pool.getClassConstant(exceptionTypeIndex).toClassType());
				}
			}

			private CatchEntry(int startPos, int exceptionTypeIndex) {
				this.startPos = startPos;
				this.exceptionTypeIndex = exceptionTypeIndex;
			}

			public int getStartPos() {
				return startPos;
			}

			public int getEndPos() {
				return endPos;
			}

			public int getEndIndex(DecompilationContext context) {
				var endPos = this.endPos;
				return endPos != NPOS ? context.posToIndex(endPos) : context.currentScope().endIndex();
			}

			public @Immutable List<ClassType> getExceptionTypes() {
				return exceptionTypes;
			}

			public boolean isFinally() {
				return exceptionTypes.isEmpty();
			}

			public boolean hasNext() {
				return hasNext;
			}

			private void setHasNext() {
				hasNext = true;
			}

			private void setEndPos(int endPos) {
				this.endPos = endPos;
			}

			private void freeze() {
				this.exceptionTypes = Collections.unmodifiableList(exceptionTypes);
			}

			private static void readTo(ExtendedDataInputStream in, ConstantPool pool, List<CatchEntry> entries, List<TryEntry> tryEntries) {

				int startPos = in.readUnsignedShort(),
						exceptionTypeIndex = in.readUnsignedShort();

				CatchEntry catchEntry = tryEntries.stream()
						.flatMap(tryEntry -> tryEntry.catchEntries.stream())
						.filter(entry -> entry.startPos == startPos).findAny()
						.orElseGet(() -> Util.addAndGetBack(entries, new CatchEntry(startPos, exceptionTypeIndex)));

				catchEntry.addExceptionType(pool, exceptionTypeIndex);
			}

			public Scope createScope(DecompilationContext context) {
				return isFinally() ?
						new FinallyScope(context, getEndIndex(context), hasNext) :
						new CatchScope(context, getEndIndex(context), exceptionTypes, hasNext);
			}

			@Override
			public int compareTo(CatchEntry other) {
				return startPos - other.startPos;
			}
		}
	}


	@Override
	public void serialize(ExtendedDataOutputStream out) {
		serializeHeader(out);
		out.writeShort(maxStackSize);
		out.writeShort(maxLocalsCount);
		out.writeByteArrayIntSized(code);
		out.write(exceptionTable);
	}
}
