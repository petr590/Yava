package x590.yava.serializable;


import x590.yava.constpool.ConstantPool;
import x590.yava.io.AssemblingOutputStream;

public interface JavaSerializableWithPool {
	void serialize(AssemblingOutputStream out, ConstantPool pool);
}
