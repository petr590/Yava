package x590.yava.testing;

import org.junit.Test;
import x590.yava.constpool.ConstantPool;

import static org.junit.Assert.assertSame;

public class ConstantPoolTest {

	@Test
	public void test() {
		assertSame(
				ConstantPool.findOrCreateConstant("ABC").getUtf8Constant(),
				ConstantPool.findOrCreateUtf8Constant("ABC")
		);
	}
}
