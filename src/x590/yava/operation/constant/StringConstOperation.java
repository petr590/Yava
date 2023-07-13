package x590.yava.operation.constant;

import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.constvalue.StringConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.main.Yava;
import x590.yava.util.StringUtil;

public final class StringConstOperation extends ConstOperation<StringConstant> {

	public StringConstOperation(StringConstant constant) {
		super(constant);
	}

	public StringConstOperation(DecompilationContext context, String value) {
		this(ConstantPool.findOrCreateConstant(value));
	}

	@Override
	public void writeTo(StringifyOutputStream out, StringifyContext context) {

		if (Yava.getConfig().multilineStringAllowed()) {
			int lnPos = getValue().indexOf('\n');

			if (lnPos != -1 && lnPos != getValue().length() - 1) {
				out.increaseIndent(2);

				String[] lines = getValue().split("\n");

				for (int i = 0, length = lines.length; ; ) {
					out.println().printIndent().print(StringUtil.stringToLiteral(lines[i] + "\n"));

					if (++i < length)
						out.write(" +");
					else
						break;
				}

				out.reduceIndent(2);

				return;
			}
		}

		out.print(StringUtil.stringToLiteral(getValue()));
	}

	public String getValue() {
		return constant.getString();
	}
}
