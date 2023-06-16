package x590.yava.testing;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.ReferenceType;
import x590.yava.type.reference.generic.AnyGenericType;
import x590.yava.type.reference.generic.GenericDeclarationType;
import x590.yava.type.reference.generic.GenericParameters;
import x590.yava.type.reference.generic.NamedGenericType;

public class GenericParametersTest {
	
	@Test
	public void testTypesReplacing() {
		var genericDeclarationType = GenericDeclarationType.of("E", List.of(ClassType.OBJECT));
		var signatureParameterType = NamedGenericType.of("T");
		
		GenericParameters<ReferenceType> parameters = GenericParameters.of(genericDeclarationType);
		
		assertEquals(GenericParameters.of(signatureParameterType),
				parameters.replaceAllTypes(Map.of(genericDeclarationType, signatureParameterType)));
	}
	
	@Test
	public void testGenericArray() {
		var unknownClassArray = ClassType.fromClassWithSignature(Class.class, AnyGenericType.INSTANCE).arrayType();
		var classArray = ClassType.CLASS.arrayType();
		
		assertEquals(unknownClassArray, unknownClassArray.castToNarrowest(classArray));
	}
}
