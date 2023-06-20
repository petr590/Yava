package x590.yava;

import x590.util.annotation.Immutable;

import java.util.Set;

public final class Keywords {

	private Keywords() {
	}

	public static final String
			BOOLEAN = "boolean",
			BYTE = "byte",
			CHAR = "char",
			SHORT = "short",
			INT = "int",
			LONG = "long",
			FLOAT = "float",
			DOUBLE = "double",
			VOID = "void",
			PUBLIC = "public",
			PROTECTED = "protected",
			PRIVATE = "private",
			STATIC = "static",
			FINAL = "final",
			ABSTRACT = "abstract",
			TRANSIENT = "transient",
			VOLATILE = "volatile",
			NATIVE = "native",
			SYNCHRONIZED = "synchronized",
			CLASS = "class",
			INTERFACE = "interface",
			ENUM = "enum",
			THIS = "this",
			SUPER = "super",
			EXTENDS = "extends",
			IMPLEMENTS = "implements",
			IMPORT = "import",
			PACKAGE = "package",
			INSTANCEOF = "instanceof",
			NEW = "new",
			IF = "if",
			ELSE = "else",
			WHILE = "while",
			DO = "do",
			FOR = "for",
			SWITCH = "switch",
			CASE = "case",
			DEFAULT = "default",
			BREAK = "break",
			CONTINUE = "continue",
			RETURN = "return",
			TRY = "try",
			CATCH = "catch",
			FINALLY = "finally",
			THROW = "throw",
			THROWS = "throws",
			ASSERT = "assert",
			TRUE = "true",
			FALSE = "false",
			NULL = "null",
			STRICTFP = "strictfp",
			CONST = "const",
			GOTO = "goto";

	public static final @Immutable Set<String> KEYWORDS = Set.of(
			BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, VOID,
			PUBLIC, PROTECTED, PRIVATE,
			STATIC, FINAL, ABSTRACT,
			TRANSIENT, VOLATILE,
			NATIVE, SYNCHRONIZED,
			CLASS, INTERFACE, ENUM,
			THIS, SUPER,
			EXTENDS, IMPLEMENTS,
			IMPORT, PACKAGE,
			INSTANCEOF, NEW,
			IF, ELSE, WHILE, DO, FOR, SWITCH, CASE, DEFAULT, BREAK, CONTINUE,
			RETURN, TRY, CATCH, FINALLY, THROW, THROWS, ASSERT,
			TRUE, FALSE, NULL,
			STRICTFP, CONST, GOTO
	);

	public static final @Immutable Set<String> MODIFIERS = Set.of(
			PUBLIC, PROTECTED, PRIVATE,
			STATIC, FINAL, ABSTRACT,
			TRANSIENT, VOLATILE,
			NATIVE, SYNCHRONIZED,
			CLASS, INTERFACE, ENUM,
			DEFAULT, STRICTFP
	);

	public static boolean isKeyword(String str) {
		return KEYWORDS.contains(str);
	}

	public static boolean isModifier(String str) {
		return MODIFIERS.contains(str);
	}
}
