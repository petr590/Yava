package x590.yava.operation.field;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.constpool.FieldrefConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.OperationUtils;

public final class PutStaticFieldOperation extends PutFieldOperation {

	private final @Nullable Int2ObjectMap<String> enumTable;

	public PutStaticFieldOperation(DecompilationContext context, int index) {
		super(context, index);
		this.enumTable = OperationUtils.getEnumTable(getDescriptor());
		init(context);
	}

	public PutStaticFieldOperation(DecompilationContext context, FieldrefConstant fieldref) {
		super(context, fieldref);
		this.enumTable = OperationUtils.getEnumTable(getDescriptor());
		init(context);
	}


	@Override
	public @Nullable Int2ObjectMap<String> getEnumTable(DecompilationContext context) {
		return enumTable;
	}


	private void init(DecompilationContext context) {
		if (!canOmit && context.getDescriptor().isStaticInitializer() &&
				context.currentScope() == context.getMethodScope() && !getValue().requiresLocalContext()) {

			if (context.getClassinfo().getField(getDescriptor()).setStaticInitializer(getValue(), context))
				this.remove();
		}

		super.initIncData(context);
		super.initGenericDescriptor(null);

		getValue().setEnumTable(enumTable);
	}

	@Override
	public void writeName(StringifyOutputStream out, StringifyContext context) {
		if (!canOmitClass(context)) {
			out.print(getDescriptor().getDeclaringClass(), context.getClassinfo()).print('.');
		}

		super.writeName(out, context);
	}

	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImport(getDescriptor().getDeclaringClass());
	}
}
