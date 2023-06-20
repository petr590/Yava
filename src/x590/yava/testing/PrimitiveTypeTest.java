package x590.yava.testing;

import org.junit.Test;
import x590.yava.exception.decompilation.IncompatibleTypesException;
import x590.yava.type.GeneralCastingKind;
import x590.yava.type.Type;
import x590.yava.type.UncertainIntegralType;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;

import static org.junit.Assert.*;
import static x590.yava.type.primitive.PrimitiveType.*;

public final class PrimitiveTypeTest {

	protected static final Type BYTE_SHORT = UncertainIntegralType.getInstance(1, 2);

	@Test
	public void testUncertainIntegralType() {
		Type type = BYTE_SHORT_INT_CHAR_BOOLEAN;

		assertEquals(BOOLEAN, type.castToNarrowest(BOOLEAN));
		assertEquals(BYTE, type.castToNarrowest(BYTE));
		assertEquals(BYTE_SHORT, type.castToNarrowest(SHORT));
		assertEquals(CHAR, type.castToNarrowest(CHAR));
		assertEquals(BYTE_SHORT_INT_CHAR, type.castToNarrowest(INT));

		assertEquals(BOOLEAN, type.castToWidest(BOOLEAN));
		assertEquals(BYTE_SHORT_INT, type.castToWidest(BYTE));
		assertEquals(SHORT_INT, type.castToWidest(SHORT));
		assertEquals(INT_CHAR, type.castToWidest(CHAR));
		assertEquals(INT, type.castToWidest(INT));

		assertEquals(SHORT, SHORT.castToWidest(BYTE));
		assertEquals(BYTE, BYTE_BOOLEAN.castToWidest(BYTE));
	}

	@Test
	public void testPrimitiveType() {
		assertThrows(IncompatibleTypesException.class, () -> BOOLEAN.castToNarrowest(BYTE));
		assertThrows(IncompatibleTypesException.class, () -> BOOLEAN.castToNarrowest(SHORT));
		assertThrows(IncompatibleTypesException.class, () -> BOOLEAN.castToNarrowest(CHAR));
		assertThrows(IncompatibleTypesException.class, () -> BOOLEAN.castToNarrowest(INT));

		assertThrows(IncompatibleTypesException.class, () -> BYTE.castToNarrowest(BOOLEAN));
		assertThrows(IncompatibleTypesException.class, () -> SHORT.castToNarrowest(BOOLEAN));
		assertThrows(IncompatibleTypesException.class, () -> CHAR.castToNarrowest(BOOLEAN));
		assertThrows(IncompatibleTypesException.class, () -> INT.castToNarrowest(BOOLEAN));

		assertThrows(IncompatibleTypesException.class, () -> BYTE.castToNarrowest(CHAR));
		assertThrows(IncompatibleTypesException.class, () -> SHORT.castToNarrowest(CHAR));
		assertThrows(IncompatibleTypesException.class, () -> INT.castToNarrowest(CHAR));

		assertEquals(BYTE, BYTE.castToNarrowest(SHORT));
		assertEquals(BYTE, BYTE.castToNarrowest(INT));

		assertThrows(IncompatibleTypesException.class, () -> SHORT.castToNarrowest(BYTE));
		assertEquals(SHORT, SHORT.castToNarrowest(INT));

		assertThrows(IncompatibleTypesException.class, () -> INT.castToNarrowest(BYTE));
		assertThrows(IncompatibleTypesException.class, () -> INT.castToNarrowest(SHORT));

		assertThrows(IncompatibleTypesException.class, () -> CHAR.castToNarrowest(BYTE));
		assertThrows(IncompatibleTypesException.class, () -> CHAR.castToNarrowest(SHORT));
		assertEquals(CHAR, CHAR.castToNarrowest(INT));
	}

	@Test
	public void testPrimitiveImplicitCast() {
		testImplicitCastToGeneral(BYTE, SHORT, INT);
		testImplicitCastToGeneral(BYTE, CHAR, INT);
		testImplicitCastToGeneral(BYTE, INT);
		testImplicitCastToGeneral(BYTE, LONG);
		testImplicitCastToGeneral(BYTE, FLOAT);
		testImplicitCastToGeneral(BYTE, DOUBLE);

		testImplicitCastToGeneral(SHORT, CHAR, INT);
		testImplicitCastToGeneral(SHORT, INT);
		testImplicitCastToGeneral(SHORT, LONG);
		testImplicitCastToGeneral(SHORT, FLOAT);
		testImplicitCastToGeneral(SHORT, DOUBLE);

		testImplicitCastToGeneral(CHAR, INT);
		testImplicitCastToGeneral(CHAR, LONG);
		testImplicitCastToGeneral(CHAR, FLOAT);
		testImplicitCastToGeneral(CHAR, DOUBLE);

		testImplicitCastToGeneral(INT, LONG);
		testImplicitCastToGeneral(INT, FLOAT);
		testImplicitCastToGeneral(INT, DOUBLE);

		testImplicitCastToGeneral(LONG, FLOAT);
		testImplicitCastToGeneral(LONG, DOUBLE);

		testImplicitCastToGeneral(FLOAT, DOUBLE);
	}

	private void testImplicitCastToGeneral(Type narrowest, Type widest) {
		testImplicitCastToGeneral(narrowest, widest, widest);
	}

	private void testImplicitCastToGeneral(Type narrowest, Type widest, Type expected) {
		assertEquals(expected, narrowest.implicitCastToGeneralNoexcept(widest, GeneralCastingKind.BINARY_OPERATOR));
		assertEquals(expected, widest.implicitCastToGeneralNoexcept(narrowest, GeneralCastingKind.BINARY_OPERATOR));
	}

	@Test
	public void testWrappedImplicitCast() {
		testWrappedImplicitCastToGeneral(BYTE, SHORT, INT);
		testWrappedImplicitCastToGeneral(BYTE, CHAR, INT);
		testWrappedImplicitCastToGeneral(BYTE, INT);
		testWrappedImplicitCastToGeneral(BYTE, LONG);
		testWrappedImplicitCastToGeneral(BYTE, FLOAT);
		testWrappedImplicitCastToGeneral(BYTE, DOUBLE);

		testWrappedImplicitCastToGeneral(SHORT, CHAR, INT);
		testWrappedImplicitCastToGeneral(SHORT, INT);
		testWrappedImplicitCastToGeneral(SHORT, LONG);
		testWrappedImplicitCastToGeneral(SHORT, FLOAT);
		testWrappedImplicitCastToGeneral(SHORT, DOUBLE);

		testWrappedImplicitCastToGeneral(CHAR, INT);
		testWrappedImplicitCastToGeneral(CHAR, LONG);
		testWrappedImplicitCastToGeneral(CHAR, FLOAT);
		testWrappedImplicitCastToGeneral(CHAR, DOUBLE);

		testWrappedImplicitCastToGeneral(INT, LONG);
		testWrappedImplicitCastToGeneral(INT, FLOAT);
		testWrappedImplicitCastToGeneral(INT, DOUBLE);

		testWrappedImplicitCastToGeneral(LONG, FLOAT);
		testWrappedImplicitCastToGeneral(LONG, DOUBLE);

		testWrappedImplicitCastToGeneral(FLOAT, DOUBLE);

		assertEquals(FLOAT, ClassType.FLOAT.implicitCastToGeneralNoexcept(INT, GeneralCastingKind.BINARY_OPERATOR));

		assertNull(ClassType.BYTE.implicitCastToGeneralNoexcept(ClassType.SHORT, GeneralCastingKind.EQUALS_COMPARISON));

		assertEquals(BOOLEAN, ClassType.BOOLEAN.implicitCastToGeneralNoexcept(ClassType.BOOLEAN, GeneralCastingKind.BINARY_OPERATOR));
		assertEquals(ClassType.BOOLEAN, ClassType.BOOLEAN.implicitCastToGeneralNoexcept(ClassType.BOOLEAN, GeneralCastingKind.TERNARY_OPERATOR));
	}

	private void testWrappedImplicitCastToGeneral(PrimitiveType narrowest, PrimitiveType widest) {
		testImplicitCastToGeneral(narrowest, widest, widest);
	}

	private void testWrappedImplicitCastToGeneral(PrimitiveType narrowest, PrimitiveType widest, PrimitiveType expected) {
		assertEquals(expected, narrowest.implicitCastToGeneralNoexcept(widest, GeneralCastingKind.BINARY_OPERATOR));
		assertEquals(expected, widest.implicitCastToGeneralNoexcept(narrowest, GeneralCastingKind.BINARY_OPERATOR));
		assertEquals(expected, narrowest.getWrapperType().implicitCastToGeneralNoexcept(widest, GeneralCastingKind.BINARY_OPERATOR));
		assertEquals(expected, widest.getWrapperType().implicitCastToGeneralNoexcept(narrowest, GeneralCastingKind.BINARY_OPERATOR));
	}

	@Test
	public void testNames() {
		testNamesFor(INT, "int", "I");
		testNamesFor(LONG, "long", "J");
	}

	private void testNamesFor(PrimitiveType type, String name, String encodedName) {
		assertEquals(name, type.getName());
		assertEquals(encodedName, type.getEncodedName());
		assertEquals(encodedName, type.getBinaryName());
	}
}
