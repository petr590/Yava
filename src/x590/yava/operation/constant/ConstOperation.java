package x590.yava.operation.constant;

import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.constvalue.ConstValueConstant;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.ReturnableOperation;
import x590.yava.util.StringUtil;

public abstract class ConstOperation<C extends ConstValueConstant> extends ReturnableOperation {

	protected final C constant;

	public ConstOperation(C constant) {
		super(constant.getType());
		this.constant = constant;
	}

	protected int flags;

	public C getConstant() {
		return constant;
	}

	@Override
	public final boolean isOne() {
		return constant.isOne();
	}

	@Override
	public void addImports(ClassInfo classinfo) {
		constant.addImports(classinfo);
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		constant.writeTo(out, context.getClassinfo(), returnType, flags);
	}

	@Override
	public void useHexNumber() {
		flags |= StringUtil.USE_HEX;
	}

	@Override
	public int getPriority() {
		return constant.getPriority();
	}

	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof ConstOperation<?> operation && constant.equals(operation.constant);
	}

	@Override
	public String toString() {
		return String.format("%s {%s}", getClass().getSimpleName(), constant);
	}
}
