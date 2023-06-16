package x590.yava.attribute;

import java.util.List;
import java.util.Objects;

import x590.yava.Importable;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.type.reference.ClassType;
import x590.yava.writable.StringifyWritable;
import x590.util.annotation.Immutable;

public class PermittedSubclassesAttribute extends Attribute implements StringifyWritable<ClassInfo>, Importable {
	
	private final @Immutable List<ClassType> subclasses;
	
	protected PermittedSubclassesAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);
		this.subclasses = in.readImmutableList(() -> pool.getClassConstant(in.readUnsignedShort()).toClassType());
	}
	
	public @Immutable List<ClassType> getSubclasses() {
		return subclasses;
	}
	
	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImportsFor(subclasses);
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo) {
		var thisType = classinfo.getThisType();
		var enclosingOrThisClass = Objects.requireNonNullElse(thisType.getEnclosingClass(), thisType);
		
		if(!subclasses.stream().allMatch(classType -> classType.isNestmateOf(enclosingOrThisClass)))
			out.print(" permits ").printAll(subclasses, classinfo, ", ");
	}
}
