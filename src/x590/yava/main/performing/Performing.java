package x590.yava.main.performing;

import x590.util.annotation.Nullable;
import x590.yava.clazz.JavaClass;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

/**
 * Класс, который предоставляет, что мы делаем с JavaClass:
 * декомиплируем, дизассемблируем, ассемблируем
 */
public interface Performing<S extends OutputStream> {

	/**
	 * Читает класс из указанного файла. Должен вызываться до {@link #setup()}
	 */
	@Nullable JavaClass read(String filename) throws IOException, UncheckedIOException;

	/**
	 * Читает класс из указанного файла. При возникновении исключений
	 * IOException или UncheckedIOException выводит их в консоль и возвращает {@code null}
	 */
	default @Nullable JavaClass readSafe(String filename) {
		try {
			return read(filename);
		} catch (IOException | UncheckedIOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Устанавливает выходной поток, если необходимо.
	 * Должен вызываться до {@link #perform(JavaClass)} и после {@link #read(String)} или {@link #readSafe(String)}
	 */
	void setup() throws IOException, UncheckedIOException;

	S getOutputStream();

	/**
	 * Осуществляет действие (например, декомпиляцию).
	 * Должен вызываться после {@link #setup()} и до {@link #afterPerforming(JavaClass)}
	 */
	void perform(JavaClass clazz);

	/**
	 * Выполняет что-то после основного действия.
	 * Должен вызываться после {@link #perform(JavaClass)} и до {@link #write(JavaClass)}
	 */
	void afterPerforming(JavaClass clazz);

	/**
	 * Записывает класс в поток.
	 * Должен вызываться после {@link #afterPerforming(JavaClass)} и до {@link #close()}
	 */
	void write(JavaClass clazz) throws IOException, UncheckedIOException;

	/**
	 * Завершающее действие.
	 * Должен вызываться после {@link #write(JavaClass)} и до {@link #close()}
	 */
	void finalizePerforming();

	/**
	 * Закрывает выходной поток.
	 * Должен вызываться после {@link #finalizePerforming()}
	 */
	void close() throws IOException, UncheckedIOException;
}
