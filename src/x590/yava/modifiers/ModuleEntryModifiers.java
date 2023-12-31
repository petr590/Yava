package x590.yava.modifiers;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.clazz.ClassInfo;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.util.IWhitespaceStringBuilder;
import x590.yava.writable.StringifyWritable;

public class ModuleEntryModifiers extends Modifiers implements StringifyWritable<ClassInfo> {

	private static final Int2ObjectMap<ModuleEntryModifiers> INSTANCES = new Int2ObjectArrayMap<>();

	protected ModuleEntryModifiers(int value) {
		super(value);
	}

	public static ModuleEntryModifiers of(int modifiers) {
		return INSTANCES.computeIfAbsent(modifiers, ModuleEntryModifiers::new);
	}

	public static ModuleEntryModifiers read(ExtendedDataInputStream in) {
		return INSTANCES.computeIfAbsent(in.readUnsignedShort(), ModuleEntryModifiers::new);
	}


	public boolean isMandated() {
		return (value & ACC_MANDATED) != 0;
	}


	@Override
	public IWhitespaceStringBuilder toStringBuilder(boolean writeHiddenModifiers, boolean disassembling) {
		return super.toStringBuilder(writeHiddenModifiers, disassembling)
				.appendIf(writeHiddenModifiers && isMandated(), "mandated");
	}

	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.print(toStringBuilder(false, false), classinfo);
	}
}
