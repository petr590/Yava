package x590.yava.exception.parsing;

public class IllegalModifierException extends ParseException {

	public IllegalModifierException() {
		super();
	}

	public IllegalModifierException(String modifier) {
		super(modifier);
	}

	public static String modifierNotAllowedHere(String modifier) {
		return "modifier \"" + modifier + "\" is not allowed here";
	}

	public static String duplicatedModifier(String modifier) {
		return "duplicated modifier \"" + modifier + "\"";
	}

	public static String conflictingModifier(String modifier) {
		return "conflicting modifier \"" + modifier + "\"";
	}
}
