package x590.yava.modifiers;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.util.IWhitespaceStringBuilder;

import static x590.yava.Keywords.*;

public final class MethodModifiers extends ClassEntryModifiers {
	
	private static final Int2ObjectMap<MethodModifiers> INSTANCES = new Int2ObjectArrayMap<>();
	
	private MethodModifiers(int value) {
		super(value);
	}
	
	public static MethodModifiers of(int modifiers) {
		return INSTANCES.computeIfAbsent(modifiers, MethodModifiers::new);
	}
	
	public static MethodModifiers read(ExtendedDataInputStream in) {
		return INSTANCES.computeIfAbsent(in.readUnsignedShort(), MethodModifiers::new);
	}

	public static MethodModifiers parse(AssemblingInputStream in) {
		return of(parseModifiers(in,
				str -> switch(str) {
					case PUBLIC -> ACC_PUBLIC;
					case PRIVATE -> ACC_PRIVATE;
					case PROTECTED -> ACC_PROTECTED;

					case STATIC -> ACC_STATIC;
					case FINAL -> ACC_FINAL;
					case ABSTRACT -> ACC_ABSTRACT;
					case SYNCHRONIZED -> ACC_SYNCHRONIZED;
					case "bridge" -> ACC_BRIDGE; // ?
					case "varargs" -> ACC_VARARGS; // ?
					case NATIVE -> ACC_NATIVE;
					case STRICTFP -> ACC_STRICTFP;
					case "synthetic" -> ACC_SYNTHETIC; // ?

					default -> -1;
				}
			));
	}


	public boolean isAbstract() {
		return (value & ACC_ABSTRACT) != 0;
	}
	
	public boolean isNative() {
		return (value & ACC_NATIVE) != 0;
	}
	
	public boolean isSynchronized() {
		return (value & ACC_SYNCHRONIZED) != 0;
	}
	
	public boolean isBridge() {
		return (value & ACC_BRIDGE) != 0;
	}
	
	public boolean isSyntheticOrBridge() {
		return (value & ACC_SYNTHETIC_OR_BRIDGE) != 0;
	}
	
	public boolean isVarargs() {
		return (value & ACC_VARARGS) != 0;
	}
	
	public boolean isStrictfp() {
		return (value & ACC_STRICTFP) != 0;
	}
	
	
	public boolean isNotAbstract() {
		return (value & ACC_ABSTRACT) == 0;
	}
	
	public boolean isNotNative() {
		return (value & ACC_NATIVE) == 0;
	}
	
	public boolean isNotSynchronized() {
		return (value & ACC_SYNCHRONIZED) == 0;
	}
	
	public boolean isNotBridge() {
		return (value & ACC_BRIDGE) == 0;
	}
	
	public boolean isNotSyntheticOrBridge() {
		return (value & ACC_SYNTHETIC_OR_BRIDGE) == 0;
	}
	
	public boolean isNotVarargs() {
		return (value & ACC_VARARGS) == 0;
	}
	
	public boolean isNotStrictfp() {
		return (value & ACC_STRICTFP) == 0;
	}
	
	
	@Override
	public IWhitespaceStringBuilder toStringBuilder(boolean forWriting) {
		return super.toStringBuilder(forWriting)
				.appendIf(isAbstract(), "abstract")
				.appendIf(isNative(), "native")
				.appendIf(isSynchronized(), "synchronized")
				.appendIf(!forWriting && isBridge(), "bridge")
				.appendIf(!forWriting && isVarargs(), "varargs")
				.appendIf(isStrictfp(), "strictfp");
	}
}
