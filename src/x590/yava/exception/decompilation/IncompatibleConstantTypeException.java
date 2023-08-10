package x590.yava.exception.decompilation;

import x590.yava.constpool.constvalue.ConstValueConstant;
import x590.yava.type.Type;

import java.io.Serial;

public class IncompatibleConstantTypeException extends DecompilationException {

	@Serial
	private static final long serialVersionUID = 5361738956185466354L;

	public IncompatibleConstantTypeException() {
		super();
	}

	public IncompatibleConstantTypeException(String message) {
		super(message);
	}

	public IncompatibleConstantTypeException(ConstValueConstant constant, Type type) {
		super("Constant " + constant.getConstantName() + " is not compatible with type " + type);
	}
}
