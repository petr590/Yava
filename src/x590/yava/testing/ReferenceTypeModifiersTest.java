package x590.yava.testing;

import static org.junit.Assert.assertTrue;
import static x590.yava.modifiers.Modifiers.*;

import org.junit.Test;

import x590.yava.modifiers.ClassModifiers;
import x590.yava.type.reference.ClassType;

public class ReferenceTypeModifiersTest {
	
	@Test
	public void testClassType() {
		ClassType string = ClassType.STRING;
		assertTrue(string.isDefinitely(ACC_PUBLIC | ACC_FINAL));
		
		System.out.println(ClassModifiers.of(String.class.getModifiers()));
		System.out.println(ClassModifiers.of(Object[].class.getModifiers()));
		System.out.println(ClassModifiers.of(byte[].class.getModifiers()));
		System.out.println(ClassModifiers.of(char[].class.getModifiers()));
	}
}
