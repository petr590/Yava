package x590.yava.modifiers;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.util.IWhitespaceStringBuilder;

public final class ModuleModifiers extends ModuleEntryModifiers {

	private static final Int2ObjectMap<ModuleModifiers> INSTANCES = new Int2ObjectArrayMap<>();

	private ModuleModifiers(int value) {
		super(value);
	}

	public static ModuleModifiers of(int modifiers) {
		return INSTANCES.computeIfAbsent(modifiers, ModuleModifiers::new);
	}

	public static ModuleModifiers read(ExtendedDataInputStream in) {
		return INSTANCES.computeIfAbsent(in.readUnsignedShort(), ModuleModifiers::new);
	}


	public boolean isOpen() {
		return (value & ACC_OPEN) != 0;
	}


	@Override
	public IWhitespaceStringBuilder toStringBuilder(boolean writeHiddenModifiers, boolean disassembling) {
		return super.toStringBuilder(writeHiddenModifiers, disassembling)
				.appendIf(isOpen(), "open");
	}
}
