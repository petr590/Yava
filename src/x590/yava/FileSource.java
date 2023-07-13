package x590.yava;

import x590.util.function.throwing.ThrowingFunction;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Источник, из которого можно загружать файлы
 */
public enum FileSource {

	/** Загружает файлы с диска */
	FILESYSTEM(Files::newInputStream),

	/** Загружает файлы из текущей JDK */
	JDK(path -> Optional.ofNullable(ClassLoader.getSystemClassLoader().getResourceAsStream(String.valueOf(path)))
			.orElseThrow(() -> new IOException(String.valueOf(path))));

	private final ThrowingFunction<Path, InputStream, IOException> inputStreamCreator;

	FileSource(ThrowingFunction<Path, InputStream, IOException> inputStreamCreator) {
		this.inputStreamCreator = inputStreamCreator;
	}

	public InputStream createInputStream(Path path) throws IOException {
		return inputStreamCreator.accept(path);
	}
}
