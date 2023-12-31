package x590.yava.constpool.constvalue;

import x590.yava.Importable;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.Constant;
import x590.yava.constpool.MethodHandleConstant;
import x590.yava.constpool.MethodTypeConstant;
import x590.yava.exception.decompilation.TypeSizeMismatchException;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.type.Type;
import x590.yava.type.TypeSize;
import x590.yava.util.StringUtil;
import x590.yava.writable.BiStringifyWritable;
import x590.yava.writable.StringifyWritable;

/**
 * Константа, описывающая какое-то константное значение - примитив или объект
 */
public abstract sealed class ConstValueConstant extends Constant
		implements StringifyWritable<ClassInfo>, BiStringifyWritable<ClassInfo, Type>, Importable
		permits ConstableValueConstant, ClassConstant, MethodTypeConstant, MethodHandleConstant {


	public abstract Type getType();


	public int intValue() {
		throw new IllegalStateException("Constant " + getConstantName() + " cannot be used as int");
	}

	public long longValue() {
		throw new IllegalStateException("Constant " + getConstantName() + " cannot be used as long");
	}

	public float floatValue() {
		throw new IllegalStateException("Constant " + getConstantName() + " cannot be used as float");
	}

	public double doubleValue() {
		throw new IllegalStateException("Constant " + getConstantName() + " cannot be used as double");
	}

	public boolean canUseAs(Type type) {
		return getType().canCastToNarrowest(type);
	}


	public abstract Operation toOperation();

	public final Operation toOperation(TypeSize size) {
		Type type = getType();

		if (size == type.getSize()) {
			return toOperation();
		}

		throw new TypeSizeMismatchException(size, type.getSize(), type);
	}

	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo, Type type) {
		writeTo(out, classinfo);
	}

	public void writeTo(StringifyOutputStream out, ClassInfo classinfo, Type type, int flags) {
		writeTo(out, classinfo, type);
	}

	@Override
	public void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo) {
		writeDisassembled(out, classinfo, getType(), StringUtil.NONE);
	}

	public abstract void writeDisassembled(DisassemblingOutputStream out, ClassInfo classinfo, Type type, int flags);


	public boolean isOne() {
		return false;
	}

	public int getPriority() {
		return Priority.DEFAULT_PRIORITY;
	}
}
