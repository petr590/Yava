package x590.yava.example.decompiling.debug;

import x590.yava.example.ExampleTesting;
import x590.yava.example.decompiling.Example;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

@Example
@SuppressWarnings("unused")
public class Debug1 {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(Debug1.class);
	}

	static String typeVarBounds(TypeVariable<?> typeVar) {
		Type[] bounds = typeVar.getBounds();
		if (bounds[0] != null) {
			return typeVar.getName();
		}

		return null;
	}
}
