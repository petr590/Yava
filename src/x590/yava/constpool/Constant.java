package x590.yava.constpool;

import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.constvalue.ClassConstant;
import x590.yava.constpool.constvalue.StringConstant;
import x590.yava.exception.WriteException;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.serializable.JavaSerializable;
import x590.yava.exception.disassembling.DisassemblingException;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.writable.DisassemblingWritable;

/**
 * Описывает константу в пуле констант
 */
public abstract class Constant implements JavaSerializable, DisassemblingWritable<ClassInfo> {

	public static final int
			TAG_UTF8                = 0x1,
			TAG_INTEGER             = 0x3,
			TAG_FLOAT               = 0x4,
			TAG_LONG                = 0x5,
			TAG_DOUBLE              = 0x6,
			TAG_CLASS               = 0x7,
			TAG_STRING              = 0x8,
			TAG_FIELDREF            = 0x9,
			TAG_METHODREF           = 0xA,
			TAG_INTERFACE_METHODREF = 0xB,
			TAG_NAME_AND_TYPE       = 0xC,
			TAG_METHOD_HANDLE       = 0xF,
			TAG_METHOD_TYPE         = 0x10,
			TAG_INVOKE_DYNAMIC      = 0x12,
			TAG_MODULE              = 0x13,
			TAG_PACKAGE             = 0x14;

	public static final String
			UTF8                = "Utf8",
			INTEGER             = "Integer",
			FLOAT               = "Float",
			LONG                = "Long",
			DOUBLE              = "Double",
			CLASS               = "Class",
			STRING              = "String",
			FIELDREF            = "Fieldref",
			METHODREF           = "Methodref",
			INTERFACE_METHODREF = "InterfaceMethodref",
			NAME_AND_TYPE       = "NameAndType",
			METHOD_HANDLE       = "MethodHandle",
			METHOD_TYPE         = "MethodType",
			INVOKE_DYNAMIC      = "InvokeDynamic",
			MODULE              = "Module",
			PACKAGE             = "Package";


	protected Constant() {}

	protected static Constant readConstant(ExtendedDataInputStream in) {
		int tag = in.readUnsignedByte();
		return switch (tag) {
			case TAG_UTF8    -> ConstantPool.findOrCreateUtf8Constant(Utf8Constant.decodeUtf8(in));
			case TAG_INTEGER -> ConstantPool.findOrCreateConstant(in.readInt());
			case TAG_FLOAT   -> ConstantPool.findOrCreateConstant(in.readFloat());
			case TAG_LONG    -> ConstantPool.findOrCreateConstant(in.readLong());
			case TAG_DOUBLE  -> ConstantPool.findOrCreateConstant(in.readDouble());

			case TAG_CLASS               -> new ClassConstant(in);
			case TAG_STRING              -> new StringConstant(in);
			case TAG_FIELDREF            -> new FieldrefConstant(in);
			case TAG_METHODREF           -> new MethodrefConstant(in);
			case TAG_INTERFACE_METHODREF -> new InterfaceMethodrefConstant(in);
			case TAG_NAME_AND_TYPE       -> new NameAndTypeConstant(in);
			case TAG_METHOD_HANDLE       -> new MethodHandleConstant(in);
			case TAG_METHOD_TYPE         -> new MethodTypeConstant(in);
			case TAG_INVOKE_DYNAMIC      -> new InvokeDynamicConstant(in);
			case TAG_MODULE              -> new ModuleConstant(in);
			case TAG_PACKAGE             -> new PackageConstant(in);

			default -> throw new DisassemblingException("Unknown tag " + tag);
		};
	}

	/**
	 * Вызывается после инициализации константы
	 */
	protected void init(ConstantPool pool) {
		// По умолчанию ничего не делает
	}

	/**
	 * LongConstant и DoubleConstant исторически занимают две позиции в пуле
	 */
	public boolean holdsTwo() {
		return false;
	}

	public abstract String getConstantName();

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		throw new WriteException("Constant " + getConstantName() + " yet not finished");
	}

	@Override
	public abstract boolean equals(Object other);

	@Override
	public abstract String toString();
}
