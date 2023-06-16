package x590.yava.variable;

/**
 * Используется для замены {@code null}, чтобы не проверять на {@code null} везде.
 */
public final class EmptyVariable implements EmptyableVariable {
	
	public static final EmptyVariable INSTANCE = new EmptyVariable();
	
	
	private EmptyVariable() {}
	
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public Variable nonEmpty() {
		throw new UnsupportedOperationException("Variable is empty");
	}
	
	@Override
	public void assignName() {}
	
	
	@Override
	public void reduceType() {}
	
	@Override
	public EmptyableVariableWrapper wrapped() {
		return VariableWrapper.empty();
	}
	
	
	@Override
	public String toString() {
		return "EmptyVariable";
	}
}
