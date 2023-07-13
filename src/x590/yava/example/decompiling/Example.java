package x590.yava.example.decompiling;

import x590.yava.FileSource;
import x590.yava.example.ExampleTesting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Позволяет обрабатывать классы примеров через аннотации
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Example {

	/**
	 * Классы, которые мы будем декомпилировать.
	 * Если массив пустой, значит, декомпилируется тот класс, в котором объявлена аннотация.<br>
	 * По умолчанию пуст.
	 */
	Class<?>[] classes() default {};

	/**
	 * Массив аргументов для запуска декомпилятора.<br>
	 * По умолчанию пуст.
	 */
	String[] args() default {};

	/**
	 * Папка, где будет поиск классов.<br>
	 * По умолчанию: {@link ExampleTesting#DEFAULT_DIR}
	 */
	String directory() default ExampleTesting.DEFAULT_DIR;

	/**
	 * Откуда брать классы.<br>
	 * По умолчанию: {@link FileSource#FILESYSTEM}
	 */
	FileSource source() default FileSource.FILESYSTEM;
}
