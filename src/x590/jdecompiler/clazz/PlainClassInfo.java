package x590.jdecompiler.clazz;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import x590.jdecompiler.field.FieldDescriptor;
import x590.jdecompiler.field.FieldInfo;
import x590.jdecompiler.method.MethodDescriptor;
import x590.jdecompiler.method.MethodInfo;
import x590.jdecompiler.modifiers.ClassModifiers;
import x590.jdecompiler.type.ClassType;
import x590.jdecompiler.type.ReferenceType;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;

public final class PlainClassInfo implements IClassInfo {
	
	private final ClassModifiers modifiers;
	private final ReferenceType thisType;
	private final @Nullable ClassType superType;
	private final @Nullable @Immutable List<ClassType> interfaces;
	private final @Immutable List<FieldInfo> fieldInfos;
	private final @Immutable List<MethodInfo> methodInfos;
	
	private PlainClassInfo(ReferenceType thisType, Class<?> clazz) {
		this.modifiers = ClassModifiers.of(clazz.getModifiers());
		this.thisType = thisType;
		this.superType = thisType.getSuperType();
		this.interfaces = thisType.getInterfaces();
		
		this.fieldInfos =
				Arrays.stream(clazz.getDeclaredFields())
					.map(field -> FieldInfo.fromReflectField(thisType, field)).toList();
		
		this.methodInfos = Stream.concat(
				Arrays.stream(clazz.getDeclaredMethods())
					.map(method -> MethodInfo.fromReflectMethod(thisType, method)),
				Arrays.stream(clazz.getDeclaredConstructors())
					.map(constructor -> MethodInfo.fromReflectConstructor(thisType, constructor))
			).toList();
	}
	
	static @Nullable PlainClassInfo fromClassType(ReferenceType thisType) {
		Class<?> clazz = thisType.getClassInstance();
		return clazz != null ? new PlainClassInfo(thisType, clazz) : null;
	}
	
	@Override
	public ClassModifiers getModifiers() {
		return modifiers;
	}
	
	@Override
	public ReferenceType getThisType() {
		return thisType;
	}
	
	@Override
	public @Nullable ClassType getSuperType() {
		return superType;
	}
	
	@Override
	public @Nullable @Immutable List<ClassType> getInterfaces() {
		return interfaces;
	}
	
	@Override
	public boolean hasFieldByDescriptor(Predicate<FieldDescriptor> predicate) {
		return fieldInfos.stream().anyMatch(fieldInfo -> predicate.test(fieldInfo.descriptor()));
	}
	
	@Override
	public boolean hasMethodByDescriptor(Predicate<MethodDescriptor> predicate) {
		return methodInfos.stream().anyMatch(methodInfo -> predicate.test(methodInfo.descriptor()));
	}
	
	@Override
	public Optional<FieldInfo> findFieldInfo(FieldDescriptor descriptor) {
		return fieldInfos.stream().filter(fieldInfo -> fieldInfo.descriptor().equals(descriptor)).findAny();
	}
	
	@Override
	public Optional<MethodInfo> findMethodInfo(MethodDescriptor descriptor) {
		return methodInfos.stream().filter(methodInfo -> methodInfo.descriptor().equals(descriptor)).findAny();
	}
}