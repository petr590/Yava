package x590.yava;

import x590.yava.attribute.AttributeType;
import x590.yava.attribute.Attributes;
import x590.yava.clazz.ClassInfo;
import x590.yava.exception.decompilation.IllegalModifiersException;
import x590.yava.io.StringifyOutputStream;
import x590.yava.main.Yava;
import x590.yava.modifiers.ClassEntryModifiers;
import x590.yava.serializable.JavaSerializable;
import x590.yava.util.IWhitespaceStringBuilder;
import x590.yava.writable.DisassemblingStringifyWritable;

import static x590.yava.modifiers.Modifiers.*;

/**
 * Представляет элемент класса - сам класс или любой из его членов
 * (поле, метод, вложенный класс)
 */
public abstract class JavaClassElement implements DisassemblingStringifyWritable<ClassInfo>, Importable {

	protected static final String ILLEGAL_ACCESS_MODIFIERS_MESSAGE = "illegal access modifiers";

	/**
	 * @return {@code true}, если этот элемент может быть записан в выходной поток, иначе {@code false}
	 */
	public boolean canStringify(ClassInfo classinfo) {
		return getModifiers().isNotSynthetic() || Yava.getConfig().showSynthetic();
	}

	public abstract ClassEntryModifiers getModifiers();


	protected static void writeAnnotations(StringifyOutputStream out, ClassInfo classinfo, Attributes attributes) {
		out.printIfNotNull(attributes.getNullable(AttributeType.RUNTIME_VISIBLE_ANNOTATIONS), classinfo)
				.printIfNotNull(attributes.getNullable(AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS), classinfo);
	}

	/**
	 * @return Строку, представляющую данный элемент (без флагов)
	 */
	public abstract String getModifiersTarget();

	protected void accessModifiersToString(ClassEntryModifiers modifiers, IWhitespaceStringBuilder str) {
		switch (modifiers.and(ACC_ACCESS_FLAGS)) {
			case ACC_VISIBLE   -> {}
			case ACC_PRIVATE   -> str.append("private");
			case ACC_PROTECTED -> str.append("protected");
			case ACC_PUBLIC    -> str.append("public");

			default -> throw new IllegalModifiersException(this, modifiers, ILLEGAL_ACCESS_MODIFIERS_MESSAGE);
		}
	}
}
