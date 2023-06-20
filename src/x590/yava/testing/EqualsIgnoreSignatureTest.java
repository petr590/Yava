package x590.yava.testing;

import org.junit.Test;
import x590.util.CollectionUtil;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.ClassType;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class EqualsIgnoreSignatureTest {

	@Test
	public void testEqualsIgnoreSignature() {
		assertTrue(CollectionUtil.collectionsEquals(
				List.of(
						ClassType.fromDescriptor("Ljava/util/Map<Ljava/lang/Class<+Ljava/lang/annotation/Annotation;>;Ljava/lang/annotation/Annotation;>;"),
						ClassType.fromDescriptor("Ljava/util/Map<Ljava/lang/Class<-Ljava/lang/annotation/Annotation;>;Ljava/lang/String;>;"),
						PrimitiveType.INT
				),
				List.of(
						ClassType.fromDescriptor("Ljava/util/Map;"),
						ClassType.fromDescriptor("Ljava/util/Map;"),
						PrimitiveType.INT
				),
				Type::equalsIgnoreSignature
		));
	}
}
