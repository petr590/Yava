package x590.yava.testing;

import org.junit.Test;
import x590.yava.type.UncertainReferenceType;

import static org.junit.Assert.*;
import static x590.yava.type.reference.ArrayType.*;

public final class ArrayTypeTest {

	@Test
	public void testCasting() {
		assertTrue(STRING_ARRAY.isDefinitelySubclassOf(OBJECT_ARRAY));
		assertTrue(OBJECT_ARRAY.isDefinitelySubclassOf(ANY_ARRAY));

		assertTrue(UncertainReferenceType.getInstance(STRING_ARRAY).isDefinitelySubtypeOf(ANY_ARRAY));
		assertTrue(OBJECT_ARRAY.arrayType().isDefinitelySubclassOf(OBJECT_ARRAY));

		assertTrue(INT_ARRAY.isDefinitelySubclassOf(ANY_ARRAY));

		assertTrue(BYTE_ARRAY.isDefinitelySubclassOf(BYTE_OR_BOOLEAN_ARRAY));
		assertTrue(BOOLEAN_ARRAY.isDefinitelySubclassOf(BYTE_OR_BOOLEAN_ARRAY));

		assertEquals(BYTE_ARRAY, BYTE_OR_BOOLEAN_ARRAY.castToNarrowestNoexcept(BYTE_ARRAY));
		assertEquals(BOOLEAN_ARRAY, BYTE_OR_BOOLEAN_ARRAY.castToNarrowestNoexcept(BOOLEAN_ARRAY));
		assertEquals(BYTE_ARRAY, BYTE_ARRAY.castToNarrowestNoexcept(BYTE_OR_BOOLEAN_ARRAY));
		assertEquals(BOOLEAN_ARRAY, BOOLEAN_ARRAY.castToNarrowestNoexcept(BYTE_OR_BOOLEAN_ARRAY));

		assertEquals(OBJECT_ARRAY, OBJECT_ARRAY.castToWidestNoexcept(STRING_ARRAY));
	}

	@Test
	public void testNames() {
		assertEquals("[I", INT_ARRAY.getBinaryName());
		assertEquals("[J", LONG_ARRAY.getBinaryName());

		assertSame(int[].class, INT_ARRAY.getClassInstance());
		assertSame(long[].class, LONG_ARRAY.getClassInstance());
	}
}
