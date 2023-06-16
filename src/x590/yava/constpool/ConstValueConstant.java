package x590.yava.constpool;

import x590.yava.Importable;
import x590.yava.clazz.ClassInfo;
import x590.yava.exception.decompilation.TypeSizeMismatchException;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.Priority;
import x590.yava.type.Type;
import x590.yava.type.TypeSize;
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
	
	
	public abstract Operation toOperation();
	
	public final Operation toOperation(TypeSize size) {
		Type type = getType();
		
		if(size == type.getSize()) {
			return toOperation();
		}
		
		throw new TypeSizeMismatchException(size, type.getSize(), type);
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo, Type type) {
		writeTo(out, classinfo);
	}
	
	
	public boolean isOne() {
		return false;
	}
	
	public int getPriority() {
		return Priority.DEFAULT_PRIORITY;
	}
}
