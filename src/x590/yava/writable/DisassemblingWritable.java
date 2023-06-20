package x590.yava.writable;

import x590.yava.io.DisassemblingOutputStream;

/**
 * Описывает объект, который можно записать в {@link DisassemblingOutputStream}
 */
public interface DisassemblingWritable<T> extends Writable<T> {
	void writeDisassembled(DisassemblingOutputStream out, T param);
}
