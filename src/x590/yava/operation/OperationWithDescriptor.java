package x590.yava.operation;

import java.util.Optional;

import x590.yava.Descriptor;
import x590.yava.MemberInfo;
import x590.yava.clazz.ClassInfo;
import x590.yava.clazz.IClassInfo;
import x590.yava.context.StringifyContext;
import x590.yava.main.Yava;
import x590.util.annotation.Nullable;

public abstract class OperationWithDescriptor<D extends Descriptor<D>> extends AbstractOperation {
	
	private final D descriptor;
	private D genericDescriptor;
	
	public OperationWithDescriptor(D descriptor) {
		this.descriptor = descriptor;
		this.genericDescriptor = descriptor;
	}
	
	/** Доступен только для прямых подклассов */
	protected void setGenericDescriptor(D genericDescriptor) {
		this.genericDescriptor = genericDescriptor; 
	}
	
	/** Инициализация generic дескриптора
	 * Метод должен вызываться для всех подклассов в конструкторе после инициализации объекта.
	 * В него передаётся объект или {@code null} для вызовов статических методов. */
	protected void initGenericDescriptor(@Nullable Operation object) {
		ClassInfo.findIClassInfo(descriptor.getDeclaringClass())
				.flatMap(iclassinfo -> findMemberInfo(iclassinfo, descriptor))
				.ifPresent(memberInfo -> setGenericDescriptor(memberInfo.getGenericDescriptor(object)));
	}
	
	protected abstract Optional<? extends MemberInfo<D, ?>> findMemberInfo(IClassInfo classinfo, D descriptor);
	
	
	public D getDescriptor() {
		return descriptor;
	}
	
	public D getGenericDescriptor() {
		return genericDescriptor;
	}
	
	protected boolean canOmitClass(StringifyContext context) {
		return context.getClassinfo().canOmitClass(descriptor);
	}
	
	protected boolean canOmitObject(StringifyContext context, Operation object) {
		return Yava.getConfig().canOmitThisAndClass() && object.isThisObject(context.getMethodModifiers());
	}
	
	protected boolean equals(OperationWithDescriptor<D> other) {
		return descriptor.equals(other.descriptor);
	}
}
