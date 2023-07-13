package x590.yava.constpool.constvalue;

import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.field.JavaField;
import x590.yava.type.Type;

import java.lang.constant.Constable;

/**
 * Константа только с одним возможным типом.
 * Такими константами являются все, кроме {@link IntegerConstant}
 */
public abstract sealed class SingleConstableValueConstant<T extends Constable>
		extends ConstableValueConstant<T>
		permits LongConstant, FloatConstant, DoubleConstant, StringConstant {

	private @Nullable JavaField constantField;
	private boolean constantSearchPerformed;

	@Override
	protected JavaField findConstantField(ClassInfo classinfo, Type type) {
		if (constantSearchPerformed)
			return constantField;

		constantSearchPerformed = true;

		return constantField = super.findConstantField(classinfo, type);
	}
}
