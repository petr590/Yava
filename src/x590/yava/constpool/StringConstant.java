package x590.yava.constpool;

import x590.yava.clazz.ClassInfo;
import x590.yava.field.FieldDescriptor;
import x590.yava.field.JavaField;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.ExtendedDataOutputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.constant.StringConstOperation;
import x590.yava.type.Type;
import x590.yava.type.reference.ClassType;
import x590.yava.util.StringUtil;
import x590.util.annotation.Nullable;

public final class StringConstant extends SingleConstableValueConstant<String> {
	
	private final int index;
	private Utf8Constant value;
	
	public StringConstant(ExtendedDataInputStream in) {
		this.index = in.readUnsignedShort();
	}
	
	public StringConstant(Utf8Constant value) {
		this.index = 0;
		this.value = value;
	}
	
	@Override
	protected void init(ConstantPool pool) {
		value = pool.get(index);
	}
	
	public Utf8Constant getUtf8Constant() {
		return value;
	}
	
	public String getString() {
		return value.getString();
	}
	
	@Override
	public String getValueAsObject() {
		return value.getString();
	}
	
	@Override
	public Type getType() {
		return ClassType.STRING;
	}
	
	@Override
	public String getConstantName() {
		return "String";
	}
	
	@Override
	public Operation toOperation() {
		return new StringConstOperation(this);
	}
	
	@Override
	public String toString() {
		return String.format("StringConstant \"%s\"", value.getString());
	}
	
	@Override
	public void serialize(ExtendedDataOutputStream out) {
		out.writeByte(TAG_STRING);
		out.writeShort(index);
	}
	
	@Override
	public void writeValue(StringifyOutputStream out, ClassInfo classinfo, Type type, boolean implicit, @Nullable FieldDescriptor descriptor) {
		out.write(StringUtil.toLiteral(value.getString()));
	}
	
	@Override
	protected boolean canUseConstant(JavaField constant) {
		return constant.getConstantValueAs(StringConstant.class).getString().equals(value.getString());
	}
	
	
	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof StringConstant constant && this.equals(constant);
	}
	
	public boolean equals(StringConstant other) {
		return this == other || this.value.equals(other.value);
	}
}
