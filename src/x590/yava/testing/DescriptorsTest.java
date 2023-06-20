package x590.yava.testing;

import org.junit.Test;
import x590.yava.method.MethodDescriptor;
import x590.yava.type.reference.ClassType;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static x590.yava.type.primitive.PrimitiveType.VOID;
import static x590.yava.type.reference.ClassType.OBJECT;

public class DescriptorsTest {

	@Test
	public void testMethodDescriptorsEquals() {
		var descriptor1 = MethodDescriptor.of(VOID, OBJECT, "method", ClassType.fromDescriptor("java/util/Map;"));
		var descriptor2 = MethodDescriptor.of(VOID, OBJECT, "method", ClassType.fromDescriptor("java/util/Map;"));
		var descriptor3 = MethodDescriptor.of(VOID, OBJECT, "method", ClassType.fromDescriptor("java/util/Map<Ljava/lang/String;Ljava/lang/String;>;"));

		assertTrue(descriptor1.equals(descriptor2));
		assertFalse(descriptor1.equals(descriptor3));
	}
}
