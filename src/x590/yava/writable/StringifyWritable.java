package x590.yava.writable;

import x590.yava.io.StringifyOutputStream;

/**
 * Описывает объект, который можно записать в {@link StringifyOutputStream}
 */
public interface StringifyWritable<T> extends Writable<T> {
	public void writeTo(StringifyOutputStream out, T param);
}
