package x590.jdecompiler.context;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

import x590.jdecompiler.ClassInfo;
import x590.jdecompiler.Importable;
import x590.jdecompiler.MethodDescriptor;
import x590.jdecompiler.exception.DecompilationException;
import x590.jdecompiler.exception.Operation;
import x590.jdecompiler.instruction.Instruction;
import x590.jdecompiler.modifiers.MethodModifiers;
import x590.jdecompiler.operation.returning.VReturnOperation;
import x590.jdecompiler.scope.MethodScope;
import x590.jdecompiler.scope.Scope;
import x590.jdecompiler.type.PrimitiveType;
import x590.jdecompiler.type.Type;
import x590.jdecompiler.type.TypeSize;
import x590.util.Logger;
import x590.util.annotation.Immutable;

public class DecompilationContext extends DecompilationAndStringifyContext implements Importable {
	
	private final OperationStack stack = new OperationStack();
	private final @Immutable Set<Operation> operations;
	private final Set<Operation> mutableOperations;
	private Scope currentScope;
	
	private final Queue<Scope> scopesQueue = new LinkedList<>();
	
	/** Для определения индекса начала выражения */
	private final int[] expressionIndexTable;
	
	/** Точки "разрыва", через которые мы не можем проводить соединение опрераций (например, инкремент и использование переменной).
	 * Нужно для корректной декомпиляции циклов и, возможно, других конструкций */
	private final Set<Integer> breaks = new HashSet<>();
	
	
	public static DecompilationContext decompile(Context otherContext, ClassInfo classinfo, MethodDescriptor descriptor, MethodModifiers modifiers, MethodScope methodScope, List<Instruction> instructions, int maxLocals) {
		return new DecompilationContext(otherContext, classinfo, descriptor, modifiers, methodScope, instructions, maxLocals);
	}
	
	private DecompilationContext(Context otherContext, ClassInfo classinfo, MethodDescriptor descriptor, MethodModifiers modifiers, MethodScope methodScope, List<Instruction> instructions, int maxLocals) {
		super(otherContext, classinfo, descriptor, methodScope, modifiers);
		
		this.currentScope = methodScope;
		
		Set<Operation> operations = new HashSet<>(instructions.size());
		this.operations = Collections.unmodifiableSet(operations);
		this.mutableOperations = operations;
		
		final Operation vreturnOperation = VReturnOperation.getInstance();
		
		var expressionIndexTable = this.expressionIndexTable = new int[instructions.size()];
		int expressionIndex = 0;
		
		for(Instruction instruction : instructions) {
			
			finalizeScopes(index);
			startScopes(index);
			
			
//			Logger.debugf("Stack: %d [%s]", index, stack.stream().map(operation -> operation.getClass().getSimpleName() + operation.getReturnType() + "]").collect(Collectors.joining(", ")));


			expressionIndexTable[index] = expressionIndex;
			
			Operation operation;
			
			try {
				operation = instruction.toOperation(this);
			} catch(Exception ex) {
				throw new DecompilationException("At index " + index, ex);
			}
			
			if(operation != null) {
				operations.add(operation);
				
				if(operation.getReturnType() == PrimitiveType.VOID) {
					expressionIndex = index;
					
					if(operation != vreturnOperation) {
						currentScope.addOperation(operation, this);
						
						if(operation instanceof Scope scope) {
							scopesQueue.add(scope);
						}
					}
					
				} else {
					push(operation);
				}
			}
			
			index++;
		}
		
		checkScopesBounds(index);
		finalizeScopes(END_INDEX);
		
		assert index == instructions.size() : index + ", " + instructions.size();
		
		index = 0;
		
		instructions.forEach(instruction -> {
			instruction.postDecompilation(this);
			index++;
		});
		
		operations.removeIf(Operation::isRemoved);
	}
	
	
	private static final int END_INDEX = Integer.MAX_VALUE;
	
	
	/** Убирает все scope-ы, которые вышли за границу видимости или были удалены. */
	private void finalizeScopes(int index) {
		String strIndex = index == END_INDEX ? "End" : Integer.toString(index);
		
		Scope currentScope = this.currentScope;
		
		while(currentScope != null && (currentScope.isRemoved() || index >= currentScope.endIndex())) {
			currentScope.deleteRemovedOperations();
			currentScope.finalizeScope(this);
			
			Logger.logf("%s: %s %s", strIndex, currentScope, currentScope.isRemoved() ? "removed" : "finalized");
			
			this.currentScope = currentScope = currentScope.superScope();
		}
		
	}
	
	/** Кладёт на стек все scope-ы, до которых дошла очередь. */
	private void startScopes(int index) {
		
		for(Iterator<Scope> iter = scopesQueue.iterator(); iter.hasNext(); ) {
			Scope scope = iter.next();
			
			if(scope.isRemoved()) {
				iter.remove();
				
			} else if(index > scope.startIndex()) {
				
				assert scope.superScope() == currentScope;
				currentScope = scope;
				
				Logger.logf("%d: %s startted", index, scope);
				
				iter.remove();
			}
		}
	}
	
	private void checkScopesBounds(int lastIndex) {
		for(Scope scope = currentScope; scope != null; scope = scope.superScope()) {
			if(scope.endIndex() > lastIndex) {
				Logger.warningFormatted("Scope %s is out of bounds of the %s", scope, methodScope);
			}
		}
	}
	
	
	public Operation pop() {
		return stack.pop();
	}
	
	public Operation popAsNarrowest(Type type) {
		return stack.popAsNarrowest(type);
	}
	
	public Operation popAsWidest(Type type) {
		return stack.popAsWidest(type);
	}
	
	public Operation popWithSize(TypeSize size) {
		return stack.popWithSize(size);
	}
	
	public Operation peek() {
		return stack.peek();
	}
	
	public Operation peek(int index) {
		return stack.peek(index);
	}
	
	public Operation peekAsNarrowest(Type type) {
		return stack.peekAsNarrowest(type);
	}
	
	public Operation peekAsWidest(Type type) {
		return stack.peekAsWidest(type);
	}
	
	public Operation peekWithSize(TypeSize size) {
		return stack.peekWithSize(size);
	}
	
	public Operation peekWithSize(int index, TypeSize size) {
		return stack.peekWithSize(index, size);
	}
	
	public void push(Operation operation) {
		mutableOperations.add(operation);
		stack.push(operation);
	}
	
	public void pushAll(Collection<Operation> operations) {
		stack.pushAll(operations);
		this.mutableOperations.addAll(operations);
	}
	
	public boolean stackEmpty() {
		return stack.empty();
	}
	
	public int stackSize() {
		return stack.size();
	}
	
	public void onNextPush(Predicate<Operation> nextPushHandler) {
		stack.onNextPush(nextPushHandler);
	}
	
	
	public Scope currentScope() {
		return currentScope;
	}
	
	public Scope superScope() {
		return currentScope.superScope();
	}
	
	
	public int expressionStartIndexByIndex(int index) {
		return expressionIndexTable[index];
	}
	
	public int currentExpressionStartIndex() {
		return expressionIndexTable[index];
	}
	
	
	public void addBreak(int index) {
		breaks.add(index);
	}
	
	public boolean hasBreak(int index) {
		return breaks.contains(index);
	}
	
	public boolean hasBreakAtCurrentIndex() {
		return breaks.contains(index);
	}
	
	
	public @Immutable Set<Operation> getOperations() {
		return operations;
	}
	
	public Iterable<Scope> getCurrentScopes() {
		
		return new Iterable<>() {
			
			public Iterator<Scope> iterator() {
				
				return new Iterator<>() {
					
					private Scope scope = currentScope;
					
					public boolean hasNext() {
						return scope != null;
					}
					
					public Scope next() {
						var scope = this.scope;
						this.scope = scope.superScope();
						return scope;
					}
					
				};
			}
		};
	}
	
	
	public void addScopeToQueue(Scope scope) {
		scopesQueue.add(scope);
	}
	
	
	@Override
	public void addImports(ClassInfo classinfo) {
		operations.forEach(operation -> operation.addImports(classinfo));
	}
	
	@Override
	public void warning(String message) {
		Logger.warning("Decompilation warning: " + message);
	}
}