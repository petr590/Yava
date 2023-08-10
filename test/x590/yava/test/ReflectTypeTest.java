package x590.yava.test;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectTypeTest {

	@Test
	public void test() throws NoSuchMethodException, SecurityException {
		Method genericMethod = ReflectTypeTest.class.getMethod("genericMethod", Object.class);

		System.out.println(genericMethod.getGenericReturnType().getClass());
		System.out.println(Arrays.toString(genericMethod.getGenericParameterTypes()));
		System.out.println(Arrays.toString(ReflectTypeTest.class.getTypeParameters()));
	}

	public Class<?> genericMethod(Object t) {
		return null;
	}
}
