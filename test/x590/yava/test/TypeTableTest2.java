package x590.yava.test;

import x590.yava.type.Type;
import x590.yava.type.UncertainIntegralType;

import java.util.ArrayList;
import java.util.List;

import static x590.yava.type.primitive.PrimitiveType.*;

public class TypeTableTest2 {

	public static void main(String[] args) {
		test(List.of(BYTE, SHORT, INT, CHAR));

		System.out.println();

		test(List.of(
				UncertainIntegralType.getInstance(1, 1),
				UncertainIntegralType.getInstance(2, 2),
				UncertainIntegralType.getInstance(4, 4),
				UncertainIntegralType.getInstance(1, 1, UncertainIntegralType.INCLUDE_CHAR)
		));
	}

	public static void test(List<Type> types1) {

		List<Type> types2 = new ArrayList<>();

		types2.add(BYTE);
		types2.add(SHORT);
		types2.add(CHAR);
		types2.add(INT);

		for (int i = 0; i < 3; i++) {
			types2.addAll(
					List.of(
							UncertainIntegralType.getInstance(1, 1, i),
							UncertainIntegralType.getInstance(1, 2, i),
							UncertainIntegralType.getInstance(1, 4, i),
							UncertainIntegralType.getInstance(2, 2, i),
							UncertainIntegralType.getInstance(2, 4, i),
							UncertainIntegralType.getInstance(4, 4, i)
					)
			);
		}

		for (Type t1 : types1) {
			for (Type t2 : types2) {
				System.out.printf("%-30s -> %-30s = %s%n", t1, t2, t1.castToWidestNoexcept(t2));
			}
		}
	}
}