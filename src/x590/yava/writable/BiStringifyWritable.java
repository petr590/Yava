package x590.yava.writable;

import x590.yava.io.StringifyOutputStream;

/**
 * Описывает объект, который можно записать в {@link StringifyOutputStream}
 */
public interface BiStringifyWritable<T, U> {
	void writeTo(StringifyOutputStream out, T param1, U param2);
}
