package x590.yava.context;

import x590.yava.clazz.ClassInfo;
import x590.yava.method.JavaMethod;
import x590.util.Logger;

public final class StringifyContext extends DecompilationAndStringifyContext {
	
	public StringifyContext(Context otherContext, ClassInfo classinfo, JavaMethod method) {
		super(otherContext, classinfo, method);
	}
	
	@Override
	public void warning(String message) {
		Logger.warning("Stringify warning " + message);
	}
}