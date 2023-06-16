package x590.yava.constpool;

import java.lang.constant.Constable;

interface ICachedConstant<T extends Constable> {
	T getValueAsObject();
}
