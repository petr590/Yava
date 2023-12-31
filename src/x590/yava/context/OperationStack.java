package x590.yava.context;

import x590.util.annotation.Immutable;
import x590.yava.exception.decompilation.DecompilationException;
import x590.yava.operation.Operation;
import x590.yava.type.Type;
import x590.yava.type.TypeSize;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class OperationStack {

	private final Deque<Operation> stack;
	private final @Immutable Collection<? extends Operation> content;

	private Predicate<? super Operation> nextPushHandler;


	public OperationStack() {
		this(new ArrayDeque<>());
	}

	public OperationStack(int initalCapacity) {
		this(new ArrayDeque<>(initalCapacity));
	}

	private OperationStack(Deque<Operation> stack) {
		this.stack = stack;
		this.content = Collections.unmodifiableCollection(stack);
	}


	public boolean isEmpty() {
		return stack.isEmpty();
	}

	public int size() {
		return stack.size();
	}


	public Operation push(Operation operation) {
		if (nextPushHandler != null) {
			if (nextPushHandler.test(operation))
				stack.push(operation);

			nextPushHandler = null;
			return operation;
		}

		stack.push(operation);
		return operation;
	}

	public void pushAll(Collection<? extends Operation> operations) {
		for (Operation operation : operations) {
			push(operation);
		}
	}


	public Operation pop() {
		try {
			return stack.pop();
		} catch (NoSuchElementException ex) {
			throw new EmptyStackException();
		}
	}

	public Operation popAsNarrowest(Type requiredType) {
		return pop().useAsNarrowest(requiredType);
	}

	public Operation popAsWidest(Type requiredType) {
		return pop().useAsWidest(requiredType);
	}

	public Operation popWithSize(TypeSize size) {
		Operation operation = pop();

		if (operation.getReturnType().getSize() == size)
			return operation;

		throw new DecompilationException("Operation size not matches: expected " + size + ", got " + operation.getReturnType().getSize() + " (operation " + operation + ")");
	}


	public Operation peek() {
		try {
			return stack.peek();
		} catch (NoSuchElementException ex) {
			throw new EmptyStackException();
		}
	}

	public Operation peek(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("index = " + index);
		}

		if (index >= stack.size()) {
			throw new IndexOutOfBoundsException("Index " + index + " out of range for stack size " + stack.size());
		}

		return stack.stream().limit(index + 1).reduce(null, (first, second) -> second);
	}

	public Operation peekAsNarrowest(Type requiredType) {
		return peek().useAsNarrowest(requiredType);
	}

	public Operation peekAsWidest(Type requiredType) {
		return peek().useAsWidest(requiredType);
	}

	public Operation peekWithSize(TypeSize size) {
		Operation operation = peek();

		if (operation.getReturnType().getSize() == size)
			return operation;

		throw new DecompilationException("Operation size not matches: expected " + size + ", got " + operation.getReturnType().getSize() + " (operation " + operation + ")");
	}

	public Operation peekWithSize(int index, TypeSize size) {
		Operation operation = peek(index);

		if (operation.getReturnType().getSize() == size)
			return operation;

		throw new DecompilationException("Operation size not matches: expected " + size + ", got " + operation.getReturnType().getSize() + " (operation " + operation + ")");
	}


	/**
	 * Назначает обработчик на следующий вызов метода {@link #push(Operation)}.
	 * Операция попадёт на стек только если обработчик вернёт {@code true}.
	 */
	public void onNextPush(Predicate<? super Operation> nextPushHandler) {
		this.nextPushHandler = nextPushHandler;
	}

	/**
	 * Сбрасывает обработчик на следующий вызов метода {@link #push(Operation)}.
	 */
	public void clearNextPushHandler() {
		this.nextPushHandler = null;
	}

	public Stream<Operation> stream() {
		return stack.stream();
	}

	@Override
	public String toString() {
		return stack.toString();
	}

	@Override
	public int hashCode() {
		return stack.hashCode();
	}

	/**
	 * @return Неизменяемую коллекцию, которая содержит все элементы стека.
	 * @apiNote Коллекцию нельзя изменить напрямую, но при изменении стека коллекция также меняется
	 */
	public @Immutable Collection<? extends Operation> getContent() {
		return content;
	}

	/**
	 * Устанавливает переданное состояние стека
	 */
	public void setState(Collection<? extends Operation> state) {
		stack.clear();
		stack.addAll(state);
	}
}
