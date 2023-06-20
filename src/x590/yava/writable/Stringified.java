package x590.yava.writable;

import x590.yava.io.StringifyOutputStream;

/**
 * Описывает объект, который можно привести к строке с помощью параметра
 */
public interface Stringified<T> extends StringifyWritable<T> {

	String toString(T param);

	@Override
	default void writeTo(StringifyOutputStream out, T param) {
		out.write(this.toString(param));
	}
}
