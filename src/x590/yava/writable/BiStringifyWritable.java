package x590.yava.writable;

import x590.yava.io.StringifyOutputStream;

/**
 * Описывает объект, который можно записать в StringifyOutputStream
 */
public interface BiStringifyWritable<T, U> {
	public void writeTo(StringifyOutputStream out, T param1, U param2);
}
