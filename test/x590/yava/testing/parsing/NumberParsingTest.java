package x590.yava.testing.parsing;

import org.junit.Test;
import x590.yava.io.AssemblingInputStream;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

public class NumberParsingTest {

	@Test
	public void test() {
		assertEquals(999, parseNumber("999"));
		// Not works
//		assertEquals(0x1p2, parseNumber("0x1p2"));
	}

	private static Number parseNumber(String str) {
		return new AssemblingInputStream(new ByteArrayInputStream(str.getBytes())).nextNumber();
	}
}
