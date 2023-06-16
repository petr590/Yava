package x590.yava.operation.variable;

import x590.yava.clazz.ClassInfo;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.AbstractOperation;
import x590.yava.operation.Operation;
import x590.yava.operation.VoidOperation;
import x590.yava.variable.Variable;

public final class VariableDefineOperation extends AbstractOperation implements VoidOperation, VariableDefinitionOperation {
	
	private final Variable variable;
	private boolean isTypeHidden;
	
	public VariableDefineOperation(Variable variable) {
		this.variable = variable.defined();
	}
	
	@Override
	public boolean isVariableDefinition() {
		return true;
	}
	
	@Override
	public Variable getVariable() {
		return variable;
	}
	
	@Override
	public void hideTypeDefinition() {
		this.isTypeHidden = true;
	}
	
	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImport(variable.getType());
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if(!isTypeHidden) {
			out.print(variable.getType(), context.getClassinfo(), variable.getName());
		} else {
			out.write(variable.getName());
		}
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof VariableDefineOperation operation &&
				variable.equals(operation.variable);
	}
}
