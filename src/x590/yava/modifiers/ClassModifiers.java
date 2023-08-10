package x590.yava.modifiers;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.exception.parsing.IllegalModifierException;
import x590.yava.exception.parsing.ParseException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.util.IWhitespaceStringBuilder;

import static x590.yava.Keywords.*;
import static x590.yava.exception.parsing.IllegalModifierException.*;

public final class ClassModifiers extends ClassEntryModifiers {

	private static final Int2ObjectMap<ClassModifiers> INSTANCES = new Int2ObjectArrayMap<>();

	private ClassModifiers(int value) {
		super(value);
	}

	public static ClassModifiers of(int modifiers) {
		return INSTANCES.computeIfAbsent(modifiers, ClassModifiers::new);
	}

	public static ClassModifiers read(ExtendedDataInputStream in) {
		return INSTANCES.computeIfAbsent(in.readUnsignedShort(), ClassModifiers::new);
	}

	// Просто для удобства парсинга
	private static final int ACC_CLASS = 0x10000;

	public static ClassModifiers parse(AssemblingInputStream in) {

		int modifiers = ACC_NONE;
		int givenModifiers = ACC_NONE;
		boolean classDefined = false;

		while (true) {
			String str = in.previewString();

			int newGivenModifiers = -1;

			int newModifiers = switch (str) {
				case PUBLIC -> ACC_PUBLIC;
				case PRIVATE -> ACC_PRIVATE;
				case PROTECTED -> ACC_PROTECTED;
				case STATIC -> ACC_STATIC;
				case FINAL -> ACC_FINAL;
				case ABSTRACT -> ACC_ABSTRACT;

				case CLASS -> {
					newGivenModifiers = ACC_CLASS;
					yield ACC_NONE;
				}

				case ENUM -> ACC_ENUM;

				case INTERFACE -> {
					newGivenModifiers = ACC_INTERFACE;
					yield ACC_ABSTRACT | ACC_INTERFACE;
				}

				case "@" -> {
					in.nextString();
					if (!in.previewString().equals(INTERFACE)) {
						throw ParseException.expectedButGot("@interface", in.previewString());
					}

					newGivenModifiers = ACC_INTERFACE | ACC_ANNOTATION;

					yield ACC_ABSTRACT | ACC_INTERFACE | ACC_ANNOTATION;
				}

				case STRICTFP -> ACC_STRICTFP;

				default -> {
					if (isModifier(str)) {
						throw new IllegalModifierException(modifierNotAllowedHere(str));
					}

					yield -1;
				}
			};

			if (newGivenModifiers == -1) {
				newGivenModifiers = newModifiers;
			}

			switch (str) {
				case CLASS, ENUM, INTERFACE, "@" -> {
					if (classDefined) {
						throw ParseException.expectedButGot("class name", str);
					}

					classDefined = true;
				}
			}

			if (newModifiers == -1) {
				break;
			}

			in.nextString();

			if ((givenModifiers & newGivenModifiers) != 0) {
				throw new IllegalModifierException(duplicatedModifier(str));
			}

			if (cantMerge(modifiers, newModifiers)) {
				throw new IllegalModifierException(conflictingModifier(str));
			}

			modifiers |= newModifiers;
			givenModifiers |= newGivenModifiers;
		}

		if (!classDefined) {
			String str = in.nextString();
			throw str.isEmpty() ?
					ParseException.expectedButGotEof("class definition") :
					ParseException.expectedButGot("class definition", str);
		}

		if ((modifiers & ACC_INTERFACE) == 0)
			modifiers |= ACC_SUPER;

		return INSTANCES.computeIfAbsent(modifiers, ClassModifiers::new);
	}


	public boolean isAbstract() {
		return (value & ACC_ABSTRACT) != 0;
	}

	public boolean isInterface() {
		return (value & ACC_INTERFACE) != 0;
	}

	public boolean isAnnotation() {
		return (value & ACC_ANNOTATION) != 0;
	}

	public boolean isEnum() {
		return (value & ACC_ENUM) != 0;
	}

	public boolean isModule() {
		return (value & ACC_MODULE) != 0;
	}

	public boolean isStrictfp() {
		return (value & ACC_STRICTFP) != 0;
	}


	public boolean isNotInterface() {
		return (value & ACC_INTERFACE) == 0;
	}

	public boolean isNotAnnotation() {
		return (value & ACC_ANNOTATION) == 0;
	}

	public boolean isNotEnum() {
		return (value & ACC_ENUM) == 0;
	}


	@Override
	public IWhitespaceStringBuilder toStringBuilder(boolean writeHiddenModifiers, boolean disassembling) {
		return super.toStringBuilder(writeHiddenModifiers, disassembling)
				.appendIf(isAbstract() && isNotInterface(), "abstract")
				.appendIf(isStrictfp(), "strictfp")
				.appendIf(isInterface() && isNotAnnotation(), "interface")
				.appendIf(isAnnotation(), "@interface")
				.appendIf(isEnum(), "enum")
				.appendIf(isModule(), "module");
	}
}
