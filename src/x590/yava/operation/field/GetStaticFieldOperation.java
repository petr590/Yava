package x590.yava.operation.field;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.FieldrefConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.field.FieldDescriptor;
import x590.yava.field.FieldInfo;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.OperationUtils;
import x590.yava.type.reference.ClassType;
import x590.yava.type.reference.WrapperClassType;
import x590.util.annotation.Nullable;

public final class GetStaticFieldOperation extends GetFieldOperation {
	
	private final boolean isPrimitiveClass;
	private @Nullable Int2ObjectMap<String> enumTable = FieldInfo.UNDEFINED_ENUM_TABLE;
	
	private static boolean isPrimitiveClass(FieldDescriptor descriptor) {
		return descriptor.getDeclaringClass().isWrapperClassType() &&
				descriptor.getType().equals(ClassType.CLASS) &&
				descriptor.getName().equals("TYPE");
	}
	
	public GetStaticFieldOperation(DecompilationContext context, int index) {
		super(context, index);
		this.isPrimitiveClass = isPrimitiveClass(getDescriptor());
	}
	
	public GetStaticFieldOperation(DecompilationContext context, FieldrefConstant fieldref) {
		super(context, fieldref);
		this.isPrimitiveClass = isPrimitiveClass(getDescriptor());
	}
	
	
	@Override
	public @Nullable Int2ObjectMap<String> getEnumTable(DecompilationContext context) {
		return OperationUtils.initEnumTable(getDescriptor(), enumTable, this::setEnumTable);
	}
	
	@Override
	public void setEnumTable(Int2ObjectMap<String> enumTable) {
		this.enumTable = enumTable;
	}
	
	
	@Override
	public void addImports(ClassInfo classinfo) {
		if(!isPrimitiveClass)
			classinfo.addImport(getDescriptor().getDeclaringClass());
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		
		if(isPrimitiveClass) {
			out.print(((WrapperClassType)getDescriptor().getDeclaringClass()).getPrimitiveType(), context.getClassinfo()).print(".class");
			
		} else {
			if(!canOmitClass(context)) {
				out.print(getDescriptor().getDeclaringClass(), context.getClassinfo()).print('.');
			}
			
			super.writeName(out, context);
		}
	}
	
	@Override
	public boolean equals(Operation other) {
		return this == other || other instanceof GetStaticFieldOperation operation &&
				super.equals(operation);
	}
}
