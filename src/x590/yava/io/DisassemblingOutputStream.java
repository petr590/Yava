package x590.yava.io;

import x590.util.LoopUtil;
import x590.util.annotation.Nullable;
import x590.util.function.TriConsumer;
import x590.yava.main.Yava;
import x590.yava.util.StringUtil;
import x590.yava.writable.DisassemblingWritable;
import x590.yava.writable.Writable;

import java.io.OutputStream;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DisassemblingOutputStream extends ExtendedOutputStream<DisassemblingOutputStream> {

	public DisassemblingOutputStream(OutputStream out) {
		super(out);
	}


	public DisassemblingOutputStream printBoolean(boolean value) {
		return print(Boolean.toString(value));
	}

	public DisassemblingOutputStream printByte(byte num) {
		return print(Byte.toString(num)).print(Yava.getConfig().useLowerSuffixes() ? 'b' : 'B');
	}

	public DisassemblingOutputStream printShort(short num) {
		return print(Short.toString(num)).print(Yava.getConfig().useLowerSuffixes() ? 's' : 'S');
	}

	public DisassemblingOutputStream printChar(char ch) {
		return print(StringUtil.charToLiteral(ch));
	}

	public DisassemblingOutputStream printInt(int num) {
		return print(Integer.toString(num));
	}

	public DisassemblingOutputStream printLong(long num) {
		return print(Long.toString(num)).print('L');
	}

	public DisassemblingOutputStream printFloat(float num) {
		if (!Float.isFinite(num)) {
			return print("Float.").print(
					num == Float.POSITIVE_INFINITY ? "POSITIVE_INFINITY" :
					num == Float.NEGATIVE_INFINITY ? "NEGATIVE_INFINITY" : "NaN"
			);
		}

		return print(Float.toString(num)).print(Yava.getConfig().getFloatSuffix());
	}

	public DisassemblingOutputStream printDouble(double num) {
		if (!Double.isFinite(num)) {
			return print("Double.").print(
					num == Double.POSITIVE_INFINITY ? "POSITIVE_INFINITY" :
					num == Double.NEGATIVE_INFINITY ? "NEGATIVE_INFINITY" : "NaN"
			);
		}

		write(Double.toString(num));

		var config = Yava.getConfig();

		return config.printDoubleSuffix() ? print(Yava.getConfig().getDoubleSuffix()) : this;
	}


	@Override
	protected <T> void write(Writable<T> writable, T param) {
		((DisassemblingWritable<T>) writable).writeDisassembled(this, param);
	}


	public <T> DisassemblingOutputStream print(DisassemblingWritable<T> writable, T param) {
		writable.writeDisassembled(this, param);
		return this;
	}

	public <T> DisassemblingOutputStream printIfNotNull(@Nullable DisassemblingWritable<T> writable, T param) {
		if (writable != null)
			writable.writeDisassembled(this, param);

		return this;
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printUsingFunction(W writable, Consumer<? super W> writer) {
		writer.accept(writable);
		return this;
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printUsingFunction(W writable, BiConsumer<? super W, ? super DisassemblingOutputStream> writer) {
		writer.accept(writable, this);
		return this;
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printUsingFunction(W writable, T param, TriConsumer<? super W, ? super DisassemblingOutputStream, ? super T> writer) {
		writer.accept(writable, this, param);
		return this;
	}


	public <T> DisassemblingOutputStream println(DisassemblingWritable<T> writable, T param) {
		return print(writable, param).print('\n');
	}

	public <T> DisassemblingOutputStream printsp(DisassemblingWritable<T> writable, T param) {
		return print(writable, param).print(' ');
	}


	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAll(Collection<? extends W> writables, T param, char delimeter) {
		return printAll(writables, param, writable -> write(delimeter));
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAll(Collection<? extends W> writables, T param, String delimeter) {
		return printAll(writables, param, writable -> write(delimeter));
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAll(Collection<? extends W> writables, T param, Consumer<? super W> delimeterWriter) {
		return printAllUsingFunction(writables, writable -> writable.writeDisassembled(this, param), delimeterWriter);
	}


	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAll(Collection<? extends W> writables, int startIndex, T param, char delimeter) {
		return printAll(writables, startIndex, param, writable -> write(delimeter));
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAll(Collection<? extends W> writables, int startIndex, T param, String delimeter) {
		return printAll(writables, startIndex, param, writable -> write(delimeter));
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAll(Collection<? extends W> writables, int startIndex, T param, Consumer<? super W> delimeterWriter) {
		LoopUtil.forEachExcludingLast(writables, writable -> writable.writeDisassembled(this, param), delimeterWriter, startIndex);
		return this;
	}


	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAllUsingFunction(Collection<? extends W> writables, Consumer<? super W> writer, char delimeter) {
		return printAllUsingFunction(writables, writer, writable -> write(delimeter));
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAllUsingFunction(Collection<? extends W> writables, Consumer<? super W> writer, String delimeter) {
		return printAllUsingFunction(writables, writer, writable -> write(delimeter));
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAllUsingFunction(Collection<? extends W> writables, Consumer<? super W> writer, Consumer<? super W> delimeterWriter) {
		LoopUtil.forEachExcludingLast(writables, writer, delimeterWriter);
		return this;
	}


	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAllUsingFunction(Collection<? extends W> writables, int startIndex, T param, char delimeter) {
		return printAllUsingFunction(writables, startIndex, param, writable -> write(delimeter));
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAllUsingFunction(Collection<? extends W> writables, int startIndex, T param, String delimeter) {
		return printAllUsingFunction(writables, startIndex, param, writable -> write(delimeter));
	}

	public <T, W extends DisassemblingWritable<T>> DisassemblingOutputStream printAllUsingFunction(Collection<? extends W> writables, int startIndex, T param, Consumer<? super W> delimeterWriter) {
		LoopUtil.forEachExcludingLast(writables, writable -> writable.writeDisassembled(this, param), delimeterWriter, startIndex);
		return this;
	}


	public <T> DisassemblingOutputStream printEach(Collection<? extends DisassemblingWritable<T>> writables, T param) {
		return printEachUsingFunction(writables, writable -> writable.writeDisassembled(this, param));
	}

	public <T> DisassemblingOutputStream printEachUsingFunction(Collection<? extends DisassemblingWritable<T>> writables, Consumer<DisassemblingWritable<T>> writer) {
		writables.forEach(writer);
		return this;
	}
}
