package x590.yava.context;

import x590.yava.attribute.CodeAttribute.ExceptionTable;
import x590.yava.clazz.ClassInfo;
import x590.yava.method.JavaMethod;
import x590.yava.method.MethodDescriptor;
import x590.yava.modifiers.MethodModifiers;
import x590.yava.scope.MethodScope;

public abstract class DecompilationAndStringifyContext extends Context {

	private final ClassInfo classinfo;
	private final JavaMethod method;

	public DecompilationAndStringifyContext(Context otherContext, ClassInfo classinfo, JavaMethod method) {
		super(otherContext);
		this.classinfo = classinfo;
		this.method = method;
	}

	public ClassInfo getClassinfo() {
		return classinfo;
	}

	public JavaMethod getMethod() {
		return method;
	}

	public MethodDescriptor getDescriptor() {
		return method.getDescriptor();
	}

	public MethodDescriptor getGenericDescriptor() {
		return method.getGenericDescriptor();
	}

	public MethodModifiers getMethodModifiers() {
		return method.getModifiers();
	}

	public MethodScope getMethodScope() {
		return method.getMethodScope();
	}

	public ExceptionTable getExceptionTable() {
		return method.getCodeAttribute().getExceptionTable();
	}
}
