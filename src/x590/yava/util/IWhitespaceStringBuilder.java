package x590.yava.util;

import x590.yava.clazz.ClassInfo;
import x590.yava.writable.DisassemblingStringifyWritable;

public interface IWhitespaceStringBuilder extends DisassemblingStringifyWritable<ClassInfo> {

	IWhitespaceStringBuilder append(String str);

	default IWhitespaceStringBuilder appendIf(boolean condition, String str) {
		return condition ? this.append(str) : this;
	}

	boolean isEmpty();

	@Override
	String toString();

	default IWhitespaceStringBuilder printTrailingSpace() {
		return this.printTrailingSpace(true);
	}

	IWhitespaceStringBuilder printTrailingSpace(boolean print);
}
