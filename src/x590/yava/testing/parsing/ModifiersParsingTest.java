package x590.yava.testing.parsing;

import org.junit.Test;

import x590.yava.exception.parsing.ParseException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.modifiers.ClassModifiers;
import x590.yava.modifiers.FieldModifiers;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static x590.yava.modifiers.Modifiers.*;

public class ModifiersParsingTest {

	@Test
	public void testClassModifiers() {
		var in = getAssemblingInputStream("public class");
		assertEquals("public", in.previewString());
		assertEquals("public", in.nextString());
		assertEquals("class", in.previewString());
		assertEquals("class", in.nextString());

		assertEquals(ACC_SUPER, parseClassModifiers("class"));
		assertEquals(ACC_SUPER | ACC_PUBLIC, parseClassModifiers("public class"));
		assertEquals(ACC_SUPER | ACC_PRIVATE, parseClassModifiers("private class"));
		assertEquals(ACC_SUPER | ACC_PROTECTED, parseClassModifiers("protected class"));
		assertEquals(ACC_SUPER | ACC_PUBLIC | ACC_ENUM, parseClassModifiers("public enum"));
		assertEquals(ACC_SUPER | ACC_ABSTRACT, parseClassModifiers("abstract class"));
		assertEquals(ACC_ABSTRACT | ACC_INTERFACE, parseClassModifiers("interface"));
		assertEquals(ACC_ABSTRACT | ACC_INTERFACE, ACC_ANNOTATION, parseClassModifiers("@interface"));

		assertThrows(ParseException.class, () -> parseClassModifiers(""));
		assertThrows(ParseException.class, () -> parseClassModifiers("@e"));
		assertThrows(ParseException.class, () -> parseClassModifiers(" {}"));
		assertThrows(ParseException.class, () -> parseClassModifiers("Foo"));
		assertThrows(ParseException.class, () -> parseClassModifiers("class class"));
		assertThrows(ParseException.class, () -> parseClassModifiers("interface class"));
	}

	private static int parseClassModifiers(String str) {
		return ClassModifiers.parse(getAssemblingInputStream(str)).getValue();
	}

	@Test
	public void testFieldModifiers() {
		assertEquals(ACC_NONE, parseFieldModifiers(""));
		assertEquals(ACC_NONE, parseFieldModifiers("int"));
		assertEquals(ACC_PUBLIC, parseFieldModifiers("public"));
		assertEquals(ACC_PRIVATE, parseFieldModifiers("private"));
		assertEquals(ACC_PROTECTED, parseFieldModifiers("protected"));
		assertEquals(ACC_PUBLIC | ACC_ENUM, parseFieldModifiers("public enum"));
		assertEquals(ACC_FINAL, parseFieldModifiers("final foo"));

		assertThrows(ParseException.class, () -> parseFieldModifiers("public private"));
		assertThrows(ParseException.class, () -> parseFieldModifiers("final final"));
		assertThrows(ParseException.class, () -> parseFieldModifiers("abstract"));
	}

	private static int parseFieldModifiers(String str) {
		return FieldModifiers.parse(getAssemblingInputStream(str)).getValue();
	}

	private static AssemblingInputStream getAssemblingInputStream(String str) {
		return new AssemblingInputStream(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
	}
}
