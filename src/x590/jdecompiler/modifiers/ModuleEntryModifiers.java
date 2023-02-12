package x590.jdecompiler.modifiers;

import x590.jdecompiler.ClassInfo;
import x590.jdecompiler.StringWritable;
import x590.jdecompiler.io.ExtendedDataInputStream;
import x590.jdecompiler.io.StringifyOutputStream;
import x590.jdecompiler.util.IWhitespaceStringBuilder;

public class ModuleEntryModifiers extends Modifiers implements StringWritable<ClassInfo> {
	
	public ModuleEntryModifiers(int value) {
		super(value);
	}
	
	public static ModuleEntryModifiers read(ExtendedDataInputStream in) {
		return new ModuleEntryModifiers(in.readUnsignedShort());
	}
	
	
	public boolean isMandated() {
		return (value & ACC_MANDATED) != 0;
	}
	
	public boolean isNotMandated() {
		return (value & ACC_MANDATED) == 0;
	}
	
	
	@Override
	public IWhitespaceStringBuilder toStringBuilder(boolean forWriting) {
		return super.toStringBuilder(forWriting)
				.appendIf(!forWriting && isMandated(), "mandated");
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.write(toStringBuilder(true), classinfo);
	}
}