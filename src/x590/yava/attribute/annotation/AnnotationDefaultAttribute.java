package x590.yava.attribute.annotation;

import x590.yava.Importable;
import x590.yava.attribute.Attribute;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.writable.StringifyWritable;

public final class AnnotationDefaultAttribute extends Attribute implements StringifyWritable<ClassInfo>, Importable {
	
	private final ElementValue value;
	
	public AnnotationDefaultAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);
		this.value = ElementValue.read(in, pool);
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		out.print(" default ").print(value, classinfo);
	}
	
	
	@Override
	public void addImports(ClassInfo classinfo) {
		value.addImports(classinfo);
	}
}
