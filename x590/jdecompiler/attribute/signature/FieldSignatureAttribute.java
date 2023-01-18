package x590.jdecompiler.attribute.signature;

import x590.jdecompiler.ClassInfo;
import x590.jdecompiler.FieldDescriptor;
import x590.jdecompiler.constpool.ConstantPool;
import x590.jdecompiler.exception.DecompilationException;
import x590.jdecompiler.io.ExtendedDataInputStream;
import x590.jdecompiler.io.ExtendedStringReader;
import x590.jdecompiler.type.ReferenceType;
import x590.jdecompiler.type.Type;

public final class FieldSignatureAttribute extends SignatureAttribute {
	
	public final ReferenceType type;
	
	public FieldSignatureAttribute(int nameIndex, String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(nameIndex, name, length);
		
		ExtendedStringReader signatureIn = new ExtendedStringReader(pool.getUtf8String(in.readUnsignedShort()));
		this.type = Type.parseSignatureParameter(signatureIn);
	}
	
	public void checkType(FieldDescriptor descriptor) {
		if(!type.baseEquals(descriptor.getType()))
			throw new DecompilationException("Field signature doesn't matches the field type: " + type + " and " + descriptor.getType());
	}
	
	@Override
	public void addImports(ClassInfo classinfo) {
		type.addImports(classinfo);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof FieldSignatureAttribute signature && this.equals(signature);
	}
	
	public boolean equals(FieldSignatureAttribute other) {
		return this == other || this.type.equals(other.type);
	}
}