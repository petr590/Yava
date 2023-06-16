package x590.yava.operation;

import x590.yava.context.StringifyContext;
import x590.yava.exception.decompilation.IncompatibleTypesException;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.cast.CastOperation;
import x590.yava.scope.Scope;
import x590.yava.type.CastingKind;
import x590.yava.type.GeneralCastingKind;
import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.IArrayType;
import x590.yava.type.reference.generic.GenericType;

public abstract class AbstractOperation implements Operation {
	
	private boolean removed, removedFromScope;
	
	@Override
	public void remove() {
		this.removedFromScope = this.removed = true;
	}
	
	@Override
	public void removeFromScope() {
		this.removedFromScope = true;
	}
	
	@Override
	public void unremove() {
		this.removed = false;
	}
	
	@Override
	public void unremoveFromScope() {
		this.removedFromScope = false;
	}
	
	@Override
	public boolean isRemoved() {
		return removed;
	}
	
	@Override
	public boolean isRemovedFromScope() {
		return removedFromScope;
	}
	
	@Override
	public final void allowImplicitCast() {
		setImplicitCast(true);
	}
	
	@Override
	public final void denyImplicitCast() {
		setImplicitCast(false);
	}
	
	protected void setImplicitCast(boolean implicitCast) {}
	
	@Override
	public final Type getReturnTypeAs(Type type, CastingKind kind) {
		Type newType = getReturnType().castTo(type, kind);
		onCastReturnType(newType, kind);
		return newType;
	}
	
	
	@Override
	public final Type getReturnTypeAsGeneralNarrowest(Operation other, GeneralCastingKind kind) {
		Type generalType = getReturnType().castToGeneral(other.getReturnType(), kind);
		this.castReturnTypeToNarrowest(generalType);
		other.castReturnTypeToNarrowest(generalType);
		return generalType;
	}
	
	@Override
	public final void castReturnTypeTo(Type type, CastingKind kind) {
		onCastReturnType(getReturnType().castTo(type, kind), kind);
	}
	
	@Override
	public Operation useAs(Type type, CastingKind kind) {
		final Type operationType = getReturnType();
		final Type castedType = operationType.castNoexcept(type, kind);
		
		if(castedType != null) {
			onCastReturnType(castedType, kind);
			return this;
		}
		
		if(canCastOperationType(operationType, type, kind)) {
			return CastOperation.of(operationType, type, false, this);
		}
		
		throw new IncompatibleTypesException(operationType, type, kind);
	}
	
	private boolean canCastOperationType(Type type1, Type type2, CastingKind kind) {
		
		if(type1.isAnyReferenceType() && type2.isReferenceType()) {
			
			if(type2 instanceof GenericType genericType) {
				return genericType.getSuperType() == null && genericType.getInterfaces() == null ||
						genericType.getSuperTypesAsStream().anyMatch(superType -> type1.canCastTo(superType, kind));
				
			} else if(type2 instanceof ClassType classType) {
				return type1.canCastTo(classType.getRawType(), kind);
				
			} else if(type1 instanceof IArrayType arrayType1 &&
					type2 instanceof IArrayType arrayType2) {
				
				int minNestLevel = Math.min(arrayType1.getNestingLevel(), arrayType2.getNestingLevel());
				
				return canCastOperationType(
						arrayType1.getNestedElementType(minNestLevel),
						arrayType2.getNestedElementType(minNestLevel),
						kind
				);
			}
		}
		
		return false;
	}
	
	
//	@Override
//	public Operation useAsGeneral(Operation other, CastingKind kind) {
//		// TODO
//	}
	
	
	protected void onCastReturnType(Type newType, CastingKind kind) {}
	
	/** Гарантирует, что операция является scope-ом */
	@Override
	public final boolean isScope() {
		return this instanceof Scope;
	}
	
	/** Оборачивает операцию в скобки, если её приоритет ниже, чем {@code thisPriority} */
	@Override
	public final void writePrioritied(StringifyOutputStream out, Operation operation, StringifyContext context, int thisPriority, Associativity associativity) {
		int otherPriority = operation.getPriority();
		
		if(otherPriority < thisPriority || (otherPriority == thisPriority && Associativity.byPriority(otherPriority) != associativity))
			out.print('(').print(operation, context).print(')');
		else
			out.print(operation, context);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof Operation operation && this.equals(operation);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}


	@Override
	public Operation clone() {
		try {
			return (Operation) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}
}
