package x590.yava;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.util.Logger;
import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.field.FieldInfo;
import x590.yava.method.MethodInfo;
import x590.yava.modifiers.ClassEntryModifiers;
import x590.yava.operation.Operation;
import x590.yava.type.Type;
import x590.yava.type.UncertainReferenceType;
import x590.yava.type.reference.ReferenceType;
import x590.yava.type.reference.generic.GenericDeclarationType;
import x590.yava.type.reference.generic.GenericParameters;

import java.util.*;

/**
 * Представляет общий шаблон для {@link FieldInfo} и {@link MethodInfo}
 */
public abstract sealed class MemberInfo<D extends Descriptor<D>, M extends ClassEntryModifiers>
		permits FieldInfo, MethodInfo {

	private final D descriptor, genericDescriptor;
	private final M modifiers;
	private @Nullable Int2ObjectMap<String> enumTable;

	public MemberInfo(D descriptor, D genericDescriptor, M modifiers) {
		this.descriptor = descriptor;
		this.genericDescriptor = genericDescriptor;
		this.modifiers = modifiers;
	}


	public D getDescriptor() {
		return descriptor;
	}

	public D getGenericDescriptor() {
		return genericDescriptor;
	}

	/**
	 * @return Конкретный generic дескриптор для переданного объекта,
	 * или generic дескриптор по умолчанию, если объект равен {@code null}.
	 */
	public D getGenericDescriptor(@Nullable Operation object) {
		if (object == null) {
			return genericDescriptor;
		}

		Type type = object.getReturnType();

		if (type instanceof ReferenceType referenceType) {
			return getDefiniteGenericDescriptor(referenceType);

		} else if (type instanceof UncertainReferenceType uncertainReferenceType) {
			return getDefiniteGenericDescriptor(uncertainReferenceType.getNarrowestType());

		} else {
			return genericDescriptor;
		}
	}


	/**
	 * @return Дженерик-дескриптор для поля или метода объекта типа {@code operationType}
	 */
	private D getDefiniteGenericDescriptor(ReferenceType operationType) {

		// Список типов в порядке от самого широкого к самому узкому
		List<ReferenceType> supertypes = new ArrayList<>();

		if (findSuperTypes(operationType, descriptor.getDeclaringClass(), supertypes)) {

			var widestClassinfo = ClassInfo.findIClassInfo(genericDescriptor.getDeclaringClass());

			if (widestClassinfo.isPresent()) {
				GenericParameters<GenericDeclarationType> srcParameters = widestClassinfo.get().getSignatureParameters();
				GenericParameters<? extends ReferenceType> parameters = srcParameters;

				for (ReferenceType currType : supertypes) {
					parameters = ReferenceType.narrowGenericParameters(currType, parameters);
				}

				final int size = parameters.size();

				assert size == srcParameters.size();

				Map<GenericDeclarationType, ReferenceType> replaceTable = new HashMap<>(size);

				for (int i = 0; i < size; i++) {
					Logger.debug(
							srcParameters.get(i),
							parameters.get(i)
									.replaceIndefiniteGenericsToDefinite()
									.replaceWildcardIndicatorsToBound(i, srcParameters)
					);

					replaceTable.put(
							srcParameters.get(i),
							parameters.get(i).replaceWildcardIndicatorsToBound(i, srcParameters)
					);
				}

				return genericDescriptor.replaceAllTypes(replaceTable);
			}
		}

		return genericDescriptor;
	}

	/**
	 * Ищет типы от {@code currentType} до {@code targetType} по иерархии и добавляет их
	 * в {@code superclasses} в порядке от самого широкого к самому узкому.
	 *
	 * @return {@code true}, если найдены супертипы от {@code currentType}
	 * до {@code targetType}, иначе {@code false}
	 */
	private boolean findSuperTypes(@Nullable ReferenceType currentType, ReferenceType targetType,
								   List<ReferenceType> superclasses) {

		if (currentType == null) {
			return false;
		}

		if (currentType.equalsIgnoreSignature(targetType)) {
			superclasses.add(currentType);
			return true;
		}

		if (findSuperTypes(currentType.getGenericSuperType(), targetType, superclasses)) {
			superclasses.add(currentType);
			return true;
		}

		var interfaces = currentType.getGenericInterfaces();

		if (interfaces != null) {
			for (ReferenceType interfaceType : interfaces) {
				if (findSuperTypes(interfaceType, targetType, superclasses)) {
					superclasses.add(currentType);
					return true;
				}
			}
		}

		return false;
	}


	public M getModifiers() {
		return modifiers;
	}

	/**
	 * @return Таблицу enum значений, необходимых для правильной работы {@code switch},
	 * или {@code null}, если член класса не содержит таблицу.
	 */
	public @Nullable Int2ObjectMap<String> getEnumTable() {
		return enumTable;
	}

	public void setEnumTable(@Nullable Int2ObjectMap<String> enumTable) {
		this.enumTable = enumTable;
	}


	@Override
	public abstract String toString();


	@Override
	public int hashCode() {
		return Objects.hash(descriptor, modifiers);
	}

	@Override
	public abstract boolean equals(Object obj);

	public boolean equals(MemberInfo<D, M> other) {
		return this == other ||
				descriptor.equals(other.descriptor) &&
						modifiers.equals(other.modifiers);
	}
}
