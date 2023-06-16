package x590.yava.util;

import x590.yava.clazz.ClassInfo;
import x590.yava.writable.DisassemblingStringifyWritable;

public interface IWhitespaceStringBuilder extends DisassemblingStringifyWritable<ClassInfo> {
	
	public IWhitespaceStringBuilder append(String str);
	
	public default IWhitespaceStringBuilder appendIf(boolean condition, String str) {
		return condition ? this.append(str) : this;
	}
	
	public boolean isEmpty();
	
	@Override
	public String toString();
	
	public default IWhitespaceStringBuilder printTrailingSpace() {
		return this.printTrailingSpace(true);
	}
	
	public IWhitespaceStringBuilder printTrailingSpace(boolean print);
}
