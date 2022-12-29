package x590.javaclass.operation.constant;

import x590.javaclass.JavaField;
import x590.javaclass.constpool.LongConstant;
import x590.javaclass.context.StringifyContext;
import x590.javaclass.io.StringifyOutputStream;
import x590.javaclass.type.PrimitiveType;
import x590.javaclass.type.Type;
import x590.javaclass.util.Util;

public class LConstOperation extends IntConvertibleConstOperation {
	
	private final long value;
	
	public LConstOperation(long value) {
		super(PrimitiveType.LONG);
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
	
	
	@Override
	public void writeValue(StringifyOutputStream out, StringifyContext context) {
		out.write(implicit && (int)value == value ? Util.toLiteral((int)value) : Util.toLiteral(value));
	}
	
	@Override
	public Type getImplicitType() {
		return (int)value == value ? PrimitiveType.INT : returnType;
	}
	
	@Override
	public boolean isOne() {
		return value == 1;
	}
	
	@Override
	protected boolean canUseConstant(JavaField constant) {
		return super.canUseConstant(constant) && ((LongConstant)constant.constantValueAttribute.value).value == value;
	}
}