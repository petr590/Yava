package x590.yava.clazz;

import x590.yava.method.JavaMethod;

import java.util.List;

/**
 * Содержит статистику декомпиляции: сколько методов в каком классе декомпилировано,
 * какие из них декомпилированы с исключениями.
 */
public interface DecompilationStats {
	void addMethodsStat(JavaClass javaClass, List<? extends JavaMethod> methodsWithExceptions, int totalMethods);
}
