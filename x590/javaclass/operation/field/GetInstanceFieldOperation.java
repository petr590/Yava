package x590.javaclass.operation.field;

import x590.javaclass.constpool.FieldrefConstant;
import x590.javaclass.context.DecompilationContext;
import x590.javaclass.context.StringifyContext;
import x590.javaclass.io.StringifyOutputStream;
import x590.javaclass.operation.Operation;

public class GetInstanceFieldOperation extends GetFieldOperation {
	
	private final Operation object;
	
	public GetInstanceFieldOperation(DecompilationContext context, int index) {
		super(context, index);
		this.object = popObject(context);
	}
	
	public GetInstanceFieldOperation(DecompilationContext context, FieldrefConstant fieldref) {
		super(context, fieldref);
		this.object = popObject(context);
	}
	
	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {
		if(!canOmitObject(context, object)) {
			out.print(object, context).print('.');
		}
		
		super.writeTo(out, context);
	}
	
	@Override
	public boolean requiresLocalContext() {
		return object.requiresLocalContext();
	}
}