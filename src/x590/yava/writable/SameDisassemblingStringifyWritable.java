package x590.yava.writable;

import x590.yava.io.DisassemblingOutputStream;
import x590.yava.io.ExtendedOutputStream;
import x590.yava.io.StringifyOutputStream;

public interface SameDisassemblingStringifyWritable<T> extends DisassemblingStringifyWritable<T> {

	@Override
	public default void writeTo(StringifyOutputStream out, T param) {
		writeTo((ExtendedOutputStream<?>)out, param);
	}
	
	@Override
	public default void writeDisassembled(DisassemblingOutputStream out, T param) {
		writeTo(out, param);
	}
	
	public void writeTo(ExtendedOutputStream<?> out, T param);
}
