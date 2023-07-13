package x590.yava.variable;

import x590.util.annotation.Nullable;
import x590.yava.scope.Scope;
import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;

import java.util.HashSet;
import java.util.Set;

import static x590.yava.Keywords.*;

public class UnnamedVariable extends AbstractVariable {

	private enum Kind {
		PLAIN, INDEX
	}

	private final Set<String> names = new HashSet<>();
	private Kind kind = Kind.PLAIN;

	public UnnamedVariable(Scope enclosingScope) {
		super(enclosingScope);
	}

	public UnnamedVariable(Scope enclosingScope, Type type) {
		super(enclosingScope, type);
	}

	public UnnamedVariable(Scope enclosingScope, boolean typeFixed) {
		super(enclosingScope, typeFixed);
	}

	public UnnamedVariable(Scope enclosingScope, Type type, boolean typeFixed) {
		super(enclosingScope, type, typeFixed);
	}

	@Override
	protected String chooseName() {
		return switch (kind) {
			case PLAIN -> names.isEmpty() ? getNameByType(type) : removeKeyword(names.iterator().next());
			case INDEX -> "i";
		};
	}

	@Override
	protected String nextName(String baseName, int index) {
		return switch (kind) {
			case PLAIN -> super.nextName(baseName, index);
			case INDEX -> {
				int num = --index / 18;
				yield (char)('i' + index - num * 18) +
						(num == 0 ? "" : Integer.toString(num + 1));
			}
		};
	}


	private static String getNameByType(Type type) {
		String name = quickGetNameByType(type);
		return name == null ? removeKeyword(type.getNameForVariable()) : name;
	}


	private static String removeKeyword(String name) {
		return switch (name) {
			case BOOLEAN      -> "bool";
			case BYTE         -> "b";
			case CHAR         -> "c";
			case SHORT        -> "s";
			case INT          -> "n";
			case LONG         -> "l";
			case FLOAT        -> "f";
			case DOUBLE       -> "d";
			case VOID         -> "v";
			case PUBLIC       -> "pub";
			case PROTECTED    -> "prot";
			case PRIVATE      -> "priv";
			case STATIC       -> "stat";
			case FINAL        -> "f";
			case ABSTRACT     -> "abs";
			case TRANSIENT    -> "trans";
			case VOLATILE     -> "vol";
			case NATIVE       -> "nat";
			case SYNCHRONIZED -> "sync";
			case CLASS        -> "clazz";
			case INTERFACE    -> "interf";
			case ENUM         -> "en";
			case THIS         -> "ts";
			case SUPER        -> "sup";
			case EXTENDS      -> "ext";
			case IMPLEMENTS   -> "impl";
			case IMPORT       -> "imp";
			case PACKAGE      -> "pack";
			case INSTANCEOF   -> "inst";
			case NEW          -> "nw";
			case IF           -> "cond";
			case ELSE         -> "els";
			case WHILE        -> "whl";
			case DO           -> "d";
			case FOR          -> "f";
			case SWITCH       -> "sw";
			case CASE         -> "cs";
			case DEFAULT      -> "def";
			case BREAK        -> "brk";
			case CONTINUE     -> "cont";
			case RETURN       -> "ret";
			case TRY          -> "tr";
			case CATCH        -> "ctch";
			case FINALLY      -> "fn";
			case THROW        -> "thr";
			case THROWS       -> "thrs";
			case ASSERT       -> "assrt";
			case TRUE         -> "tr";
			case FALSE        -> "fls";
			case NULL         -> "nul";
			case STRICTFP     -> "strict";
			case CONST        -> "cnst";
			case GOTO         -> "gt";
			default           -> name;
		};
	}


	private static @Nullable String quickGetNameByType(Type type) {
		if (type instanceof ClassType classType) {
			return switch (classType.getSimpleName()) {
				case "Object"        -> "obj";
				case "Class"         -> "clazz";
				case "Byte"          -> "b";
				case "Character"     -> "c";
				case "Short"         -> "s";
				case "Integer"       -> "n";
				case "Long"          -> "l";
				case "Float"         -> "f";
				case "Double"        -> "d";
				case "Boolean"       -> "bool";
				case "String"        -> "str";
				case "Enum"          -> "en";
				case "StringBuilder" -> "strBuilder";
				case "StringBuffer"  -> "strBuffer";
				case "BigInteger"    -> "bigint";
				case "BigDecimal"    -> "bigdec";
				case "Void"          -> "v";
				default              -> null;

			};
		}

		return null;
	}


	@Override
	public String getPossibleName() {
		return names.isEmpty() ? null : names.iterator().next();
	}

	@Override
	public void addPossibleName(@Nullable String name) {
		if (name != null)
			names.add(name);
	}

	@Override
	public void makeAnIndex() {
		kind = Kind.INDEX;
	}


	@Override
	public String toString() {
		return String.format("UnnamedVariable #%x { type = %s, names = %s, enclosingScope = %s }",
				hashCode(), type, names, getEnclosingScope());
	}
}
