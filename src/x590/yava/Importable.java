package x590.yava;

import x590.yava.clazz.ClassInfo;

/**
 * Описывает объект, который может импортировать какие-то классы
 */
public interface Importable {
	default void addImports(ClassInfo classinfo) {}
}
