package x590.yava.testing;

import org.junit.Before;
import org.junit.Test;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.operation.returning.VReturnOperation;
import x590.yava.scope.Scope;
import x590.yava.type.Type;
import x590.yava.type.primitive.PrimitiveType;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScopeTest {

	private Scope scope;

	private final Operation
			operation1 = operation1(),
			operation2 = operation2();


	@Before
	public void setup() {
		scope = new Scope(5, 10, null, Collections.emptyList()) {};
	}


	@Test
	public void testIndexes() {
		scope.addOperation(operation1, 6);
		scope.addOperation(operation2, 8);

		assertEquals(List.of(operation2), scope.pullOperationsFromIndex(7));
		assertEquals(List.of(operation1), scope.getOperations());
	}

	@Test
	public void testEndIndexes() {
		scope.addOperation(operation1, 6);
		scope.addOperation(operation2, 8);

		assertEquals(List.of(), scope.pullOperationsFromIndex(9));
		assertEquals(List.of(operation1, operation2), scope.getOperations());
	}

	@Test
	public void testEmptyIndexes() {
		assertEquals(List.of(), scope.pullOperationsFromIndex(9));
		assertEquals(List.of(), scope.getOperations());
	}


	private Operation operation1() {
		return new AbstractOperation() {

			@Override
			public void writeTo(StringifyOutputStream out, StringifyContext context) {
				out.write("AbstractOperation");
			}

			@Override
			public Type getReturnType() {
				return PrimitiveType.VOID;
			}

			@Override
			public String toString() {
				return "AbstractOperation";
			}

			@Override
			public boolean equals(Operation other) {
				return this == other;
			}
		};
	}

	private Operation operation2() {
		return VReturnOperation.getInstance();
	}
}
