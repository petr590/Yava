package x590.yava.testing.parsing;

import org.junit.Test;
import x590.yava.io.AssemblingInputStream;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

public class AssemblingInputStreamTest {

	@Test
	public void test() {
		var in = new AssemblingInputStream(new ByteArrayInputStream("abcd {}()".getBytes()));

		assertEquals("abcd", in.nextString());
		assertFalse(in.advanceIfHasNext("("));
		assertTrue(in.advanceIfHasNext("{"));
		assertTrue(in.advanceIfHasNext("}"));
		assertTrue(in.advanceIfHasNext("()"));
	}
}
