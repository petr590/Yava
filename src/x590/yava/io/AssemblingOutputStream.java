package x590.yava.io;

import x590.util.annotation.Nullable;
import x590.util.io.UncheckedDataOutputStream;
import x590.yava.constpool.ConstantPool;
import x590.yava.serializable.JavaSerializable;
import x590.yava.exception.WriteException;
import x590.yava.serializable.JavaSerializableWithPool;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AssemblingOutputStream extends UncheckedDataOutputStream<AssemblingOutputStream> {

	public AssemblingOutputStream(OutputStream out) {
		super(out);
	}

	public AssemblingOutputStream(DataOutputStream out) {
		super(out);
	}

	public void write(JavaSerializable serializable) {
		serializable.serialize(this);
	}

	public void write(JavaSerializableWithPool serializable, ConstantPool pool) {
		serializable.serialize(this, pool);
	}

	public void writeIfNotNull(@Nullable JavaSerializable serializable) {
		if (serializable != null)
			serializable.serialize(this);
	}

	public void writeIfNotNull(@Nullable JavaSerializableWithPool serializable, ConstantPool pool) {
		if(serializable != null)
			serializable.serialize(this, pool);
	}

	/**
	 * Записывает массив байт
	 */
	public void writeByteArray(byte[] data) {
		write(data);
	}

	/**
	 * Записывает длину массива как {@code short}, затем записывает сам массив.
	 * @throws WriteException если длина массива превышает пределы {@code short}
	 */
	public void writeByteArraySized(byte[] data) {
		writeShortSize(data.length);
		write(data);
	}

	public void writeShorts(int[] values) {
		for(int value : values) {
			writeShort(value);
		}
	}

	/**
	 * Записывает размер списка как {@code short}, затем записывает сам список.
	 * @throws WriteException если размер списка превышает пределы {@code short}
	 */
	public void writeAll(Collection<? extends JavaSerializable> serializables) {
		writeShortSize(serializables.size());
		writeAllNoSized(serializables);
	}

	/**
	 * Записывает {@code size} как {@code short}, затем записывает список.
	 * @throws WriteException если {@code size} превышает пределы {@code short}
	 */
	public void writeAll(int size, Collection<? extends JavaSerializableWithPool> serializables, ConstantPool pool) {
		writeShortSize(size);
		writeAllNoSized(serializables, pool);
	}

	/**
	 * Записывает размер списка как {@code short}, затем записывает сам список.
	 * @throws WriteException если размер списка превышает пределы {@code short}
	 */
	public void writeAll(Collection<? extends JavaSerializableWithPool> serializables, ConstantPool pool) {
		writeShortSize(serializables.size());
		writeAllNoSized(serializables, pool);
	}


	public void writeAllNoSized(Collection<? extends JavaSerializable> serializables) {
		for (JavaSerializable serializable : serializables)
			serializable.serialize(this);
	}

	public void writeAllNoSized(Collection<? extends JavaSerializableWithPool> serializables, ConstantPool pool) {
		for (JavaSerializableWithPool serializable : serializables)
			serializable.serialize(this, pool);
	}

	/**
	 * Записывает размер как {@code short}
	 * @throws WriteException если {@code size} превышает пределы {@code short}
	 */
	public void writeShortSize(int size) {
		if (size > 0xFFFF || size < 0)
			throw new WriteException("Size (" + size + ") exceeds the short limit");
		writeShort(size);
	}


	public AssemblingOutputStream record(JavaSerializable serializable) {
		write(serializable);
		return this;
	}


	public AssemblingOutputStream record(JavaSerializableWithPool serializable, ConstantPool pool) {
		write(serializable, pool);
		return this;
	}

	public AssemblingOutputStream recordIfNotNull(@Nullable JavaSerializable serializable) {
		writeIfNotNull(serializable);
		return this;
	}

	public AssemblingOutputStream recordIfNotNull(@Nullable JavaSerializableWithPool serializable, ConstantPool pool) {
		writeIfNotNull(serializable, pool);
		return this;
	}

	/**
	 * Записывает массив байт
	 */
	public AssemblingOutputStream recordByteArray(byte[] data) {
		write(data);
		return this;
	}

	/**
	 * Записывает длину массива как {@code short}, затем записывает сам массив.
	 * @throws WriteException если длина массива превышает пределы {@code short}
	 */
	public AssemblingOutputStream recordByteArraySized(byte[] data) {
		writeByteArraySized(data);
		return this;
	}

	public AssemblingOutputStream recordShorts(int[] values) {
		writeShortSize(values.length);
		writeShorts(values);
		return this;
	}

	/**
	 * Записывает размер списка как {@code short}, затем записывает сам список.
	 * @throws WriteException если размер списка превышает пределы {@code short}
	 */
	public AssemblingOutputStream recordAll(Collection<? extends JavaSerializable> serializables) {
		writeShortSize(serializables.size());
		writeAllNoSized(serializables);
		return this;
	}

	/**
	 * Записывает {@code size} как {@code short}, затем записывает поток.
	 * @throws WriteException если размер списка превышает пределы {@code short}
	 */
	public AssemblingOutputStream recordAllAsShorts(int size, IntStream shorts) {
		writeShortSize(size);
		shorts.forEachOrdered(this::recordShort);
		return this;
	}

	/**
	 * Записывает размер списка как {@code short}, затем записывает сам список.
	 * @throws WriteException если размер списка превышает пределы {@code short}
	 */
	public AssemblingOutputStream recordAll(Collection<? extends JavaSerializableWithPool> serializables, ConstantPool pool) {
		writeShortSize(serializables.size());
		writeAllNoSized(serializables, pool);
		return this;
	}


	public AssemblingOutputStream recordAllNoSized(Collection<? extends JavaSerializable> serializables) {
		for (JavaSerializable serializable : serializables)
			serializable.serialize(this);

		return this;
	}

	public AssemblingOutputStream recordAllNoSized(Collection<? extends JavaSerializableWithPool> serializables, ConstantPool pool) {
		for (JavaSerializableWithPool serializable : serializables)
			serializable.serialize(this, pool);

		return this;
	}

	public AssemblingOutputStream recordShortSize(int size) {
		writeShortSize(size);
		return this;
	}
}
