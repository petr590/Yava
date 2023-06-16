package x590.yava.modifiers;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import x590.yava.exception.parsing.IllegalModifierException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.util.IWhitespaceStringBuilder;
import x590.yava.util.WhitespaceStringBuilder;
import x590.util.IntegerUtil;

import java.util.function.ToIntFunction;

import static x590.yava.Keywords.isModifier;
import static x590.yava.exception.parsing.IllegalModifierException.*;

public abstract class Modifiers {
	
	public static final int
			ACC_NONE         = 0x0000,
			ACC_VISIBLE      = 0x0000, // class, field, method
			ACC_PUBLIC       = 0x0001, // class, field, method
			ACC_PRIVATE      = 0x0002, // nested class, field, method
			ACC_PROTECTED    = 0x0004, // nested class, field, method
			ACC_STATIC       = 0x0008, // nested class, field, method
			ACC_FINAL        = 0x0010, // class, field, method
			ACC_SUPER        = 0x0020, // class (deprecated)
			ACC_SYNCHRONIZED = 0x0020, // method
			ACC_VOLATILE     = 0x0040, // field
			ACC_TRANSIENT    = 0x0080, // field
			ACC_BRIDGE       = 0x0040, // method
			ACC_VARARGS      = 0x0080, // method
			ACC_NATIVE       = 0x0100, // method
			ACC_INTERFACE    = 0x0200, // class
			ACC_ABSTRACT     = 0x0400, // class, method
			ACC_STRICTFP     = 0x0800, // class, method
			ACC_SYNTHETIC    = 0x1000, // class, field, method
			ACC_ANNOTATION   = 0x2000, // class
			ACC_ENUM         = 0x4000, // class, field
			
			ACC_MODULE       = 0x8000, // module class
			ACC_OPEN         = 0x0020, // module attribute
			ACC_MANDATED     = 0x8000, // module entry
			ACC_TRANSITIVE   = 0x0020, // module requirement
			ACC_STATIC_PHASE = 0x0040, // module requirement
			
			ACC_ACCESS_FLAGS = ACC_VISIBLE | ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED,
			ACC_SYNTHETIC_OR_BRIDGE = ACC_SYNTHETIC | ACC_BRIDGE;


	private static final Int2IntMap ILLEGAL_MODIFIERS = new Int2IntArrayMap();

	static {
		ILLEGAL_MODIFIERS.put(ACC_PUBLIC, ACC_PRIVATE | ACC_PROTECTED);
		ILLEGAL_MODIFIERS.put(ACC_PRIVATE, ACC_PUBLIC | ACC_PROTECTED);
		ILLEGAL_MODIFIERS.put(ACC_PROTECTED, ACC_PUBLIC | ACC_PRIVATE);

		addBothModifiers(ACC_FINAL, ACC_ABSTRACT);
		addBothModifiers(ACC_FINAL, ACC_VOLATILE);
		addBothModifiers(ACC_NATIVE, ACC_STRICTFP);
	}


	public static boolean canMerge(int modifiers, int newModifiers) {
		for(int offset = 0, modifier = newModifiers; modifier != 0; modifier >>>= 1, offset++) {
			if((modifier & 0x1) != 0 &&
					(ILLEGAL_MODIFIERS.getOrDefault(0x1 << offset, 0) & modifiers) != 0) {

				return false;
			}
		}

		return true;
	}


	protected static int parseModifiers(AssemblingInputStream in, ToIntFunction<String> function) {
		int modifiers = ACC_NONE;

		while(true) {
			String str = in.previewString();

			int newModifiers = function.applyAsInt(str);

			if(newModifiers == -1) {
				if(isModifier(str)) {
					throw new IllegalModifierException(modifierNotAllowedHere(str));
				}

				break;
			}

			in.nextString();

			if((modifiers & newModifiers) != 0) {
				throw new IllegalModifierException(duplicatedModifier(str));
			}

			if(!canMerge(modifiers, newModifiers)) {
				throw new IllegalModifierException(conflictingModifier(str));
			}

			modifiers |= newModifiers;
		}

		return modifiers;
	}


	private static void addBothModifiers(int modifier1, int modifier2) {
		ILLEGAL_MODIFIERS.put(modifier1, modifier2);
		ILLEGAL_MODIFIERS.put(modifier2, modifier1);
	}
	
	
	protected final int value;
	
	public Modifiers(int value) {
		this.value = value;
	}
	
	
	public int getValue() {
		return value;
	}
	
	
	public boolean isSynthetic() {
		return (value & ACC_SYNTHETIC) != 0;
	}
	
	
	public boolean isNotSynthetic() {
		return (value & ACC_SYNTHETIC) == 0;
	}
	
	
	public boolean allOf(int modifiers) {
		return (value & modifiers) == modifiers;
	}
	
	public boolean anyOf(int modifiers) {
		return (value & modifiers) != 0;
	}
	
	public boolean notAllOf(int modifier) {
		return (value & modifier) != modifier;
	}
	
	public boolean noneOf(int modifier) {
		return (value & modifier) == 0;
	}


	public int and(int modifier) {
		return value & modifier;
	}
	
	
	public String toHex() {
		return IntegerUtil.hex4(value);
	}
	
	public String toHexWithPrefix() {
		return IntegerUtil.hex4WithPrefix(value);
	}
	
	
	protected IWhitespaceStringBuilder toStringBuilder(boolean forWriting) {
		return new WhitespaceStringBuilder().printTrailingSpace(forWriting)
				.appendIf(!forWriting && isSynthetic(), "synthetic");
	}
	
	public String toSimpleString() {
		return toStringBuilder(false).toString();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " { " + toStringBuilder(false).toString() + " }";
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof Modifiers modifiers && this.equals(modifiers);
	}
	
	public boolean equals(Modifiers other) {
		return value == other.value;
	}
}
