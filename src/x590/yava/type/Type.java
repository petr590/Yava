package x590.yava.type;

import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.util.io.UncheckedInputStream;
import x590.yava.Importable;
import x590.yava.clazz.ClassInfo;
import x590.yava.clazz.IClassInfo;
import x590.yava.exception.decompilation.IncompatibleTypesException;
import x590.yava.exception.disassembling.InvalidMethodDescriptorException;
import x590.yava.exception.disassembling.InvalidTypeNameException;
import x590.yava.io.ExtendedStringInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.type.primitive.IntegralType;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.*;
import x590.yava.type.reference.generic.*;
import x590.yava.writable.BiStringifyWritable;
import x590.yava.writable.SameDisassemblingStringifyWritable;

import java.util.*;
import java.util.function.Function;

/**
 * Класс, описывающий тип в Java: int, double, String и т.д.
 */

@Immutable
public abstract class Type implements
		SameDisassemblingStringifyWritable<ClassInfo>,
		BiStringifyWritable<ClassInfo, String>, Importable {

	/**
	 * Записывает себя и имя переменной через пробел.
	 * Если включены c-style массивы, то массив записывается как в C
	 */
	@Override
	public void writeTo(StringifyOutputStream out, ClassInfo classinfo, String name) {
		writeLeftDefinition(out, classinfo);
		out.printsp().write(name);
		writeRightDefinition(out, classinfo);
	}

	/**
	 * Записывает левую часть объявления массива, если включены c-style массивы.
	 * Если нет, то работает просто как {@link #writeTo(StringifyOutputStream, Object)}
	 */
	public void writeLeftDefinition(StringifyOutputStream out, ClassInfo classinfo) {
		writeTo(out, classinfo);
	}

	/**
	 * Записывает правую часть объявления массива, если включены c-style массивы.
	 * Если нет, то ничего не делает
	 */
	public void writeRightDefinition(StringifyOutputStream out, ClassInfo classinfo) {}


	/**
	 * @return Самый вложенный тип массива, если {@code Yava.getInstance().useCStyleArray() == true}.
	 * Иначе возвращает {@code this}. Для всех типов, не являющихся массивами, возвращает {@code this}
	 */
	public Type getArrayMemberIfUsingCArrays() {
		return this;
	}


	@Override
	public abstract String toString();

	/**
	 * @return Закодированное имя типа: "Ljava/lang/Object;", "I".<br>
	 * Используется также для сравнения типов и для получения хеш-кода
	 */
	public abstract String getEncodedName();

	/**
	 * @return Имя типа: "java.lang.Object", "int"
	 */
	public abstract String getName();

	/**
	 * @return Имя скомпилированного типа без сигнатуры.
	 * Например, для типа "java/util/Map$Entry" вернёт "java.util.Map$Entry"
	 */
	public @Nullable String getBinaryName() {
		return null;
	}

	/**
	 * Имя для переменной. Например, все переменные типа int называются "n".
	 * Если таких переменных больше одной, то к названиям добавляется номер.
	 *
	 * @return Имя переменной (без номера)
	 */
	public abstract String getNameForVariable();


	/**
	 * Гарантирует, что объект - экземпляр класса {@link PrimitiveType}
	 */
	public final boolean isPrimitive() {
		return this instanceof PrimitiveType;
	}

	/**
	 * Гарантирует, что объект - экземпляр класса {@link IntegralType}
	 */
	public final boolean isIntegral() {
		return this instanceof IntegralType;
	}


	/**
	 * Для всех ссылочных типов, в том числе и для специальных
	 */
	public boolean isAnyReferenceType() {
		return isReferenceType();
	}


	/**
	 * Гарантирует, что объект - экземпляр класса {@link ReferenceType}
	 */
	public final boolean isReferenceType() {
		return this instanceof ReferenceType;
	}

	/**
	 * Гарантирует, что объект - экземпляр класса {@link ClassType}
	 */
	public final boolean isClassType() {
		return this instanceof ClassType;
	}

	/**
	 * Гарантирует, что объект - экземпляр класса {@link WrapperClassType}
	 */
	public final boolean isWrapperClassType() {
		return this instanceof WrapperClassType;
	}

	/**
	 * Гарантирует, что объект - экземпляр класса {@link ArrayType}
	 */
	public final boolean isArrayType() {
		return this instanceof ArrayType;
	}

	/**
	 * Гарантирует, что объект - экземпляр класса {@link IArrayType}
	 */
	public final boolean isIArrayType() {
		return this instanceof IArrayType;
	}

	/**
	 * Гарантирует, что объект - экземпляр класса UncertainReferenceType
	 */
	public final boolean isUncertainReferenceType() {
		return this instanceof UncertainReferenceType;
	}

	/**
	 * Для всех generic типов
	 */
	public boolean isGenericType() {
		return false;
	}


	/**
	 * Проверяет, что это тип {@link PrimitiveType#BYTE}, {@link PrimitiveType#SHORT} или {@link PrimitiveType#CHAR}
	 */
	public final boolean isByteOrShortOrChar() {
		return this == PrimitiveType.BYTE || this == PrimitiveType.SHORT || this == PrimitiveType.CHAR;
	}

	/**
	 * Проверяет, что это тип {@link PrimitiveType#LONG}, {@link PrimitiveType#FLOAT} или {@link PrimitiveType#DOUBLE}
	 */
	public final boolean isLongOrFloatOrDouble() {
		return this == PrimitiveType.LONG || this == PrimitiveType.FLOAT || this == PrimitiveType.DOUBLE;
	}


	public final boolean canUseShortOperator() {
		return this.isPrimitive() || this.isWrapperClassType();
	}


	/**
	 * Размер типа на стеке
	 */
	public abstract TypeSize getSize();

	/**
	 * @return Тип массива, элементом которого является текущий тип.
	 */
	public IArrayType arrayType() {
		return arrayType(1);
	}

	/**
	 * @param nestingLevel уровень вложенности массива. Должен быть не меньше 1
	 * @return Тип массива, элементом которого является текущий тип.
	 */
	public IArrayType arrayType(int nestingLevel) {
		return ArrayType.forType(this, nestingLevel);
	}


	public Type arrayTypeAsType() {
		return arrayType().asType();
	}

	public Type arrayTypeAsType(int nestingLevel) {
		return arrayType(nestingLevel).asType();
	}


	/**
	 * @return {@code true}, если {@code this} является подтипом {@code other},
	 * {@code false} в противном случае или если это нельзя точно определить
	 */
	public boolean isDefinitelySubtypeOf(Type other) {
		return canCastToNarrowest(other);
	}


	/**
	 * @return {@code true}, если возможно преобразовать {@code this} в {@code other}
	 */
	public final boolean canCastToNarrowest(Type other) {
		if (canCastToNarrowestImpl(other))
			return true;

		return other.canReversedCastToNarrowestImpl(this);
	}

	/**
	 * @return {@code true}, если возможно преобразовать {@code this} в {@code other}
	 */
	public final boolean canCastToWidest(Type other) {
		if (canCastToWidestImpl(other))
			return true;

		return other.canReversedCastToWidestImpl(this);
	}

	public final boolean canCastTo(Type rawType, CastingKind kind) {
		return kind.isNarrowest() ?
				canCastToNarrowest(rawType) :
				canCastToWidest(rawType);
	}


	/**
	 * Для оптимизации (чтобы не создавать новые экземпляры типов при простой проверке)
	 */
	protected boolean canCastToNarrowestImpl(Type other) {
		return this.castImpl(other, CastingKind.NARROWEST) != null;
	}

	/**
	 * Для оптимизации (чтобы не создавать новые экземпляры типов при простой проверке)
	 */
	protected boolean canReversedCastToNarrowestImpl(Type other) {
		return this.reversedCastImpl(other, CastingKind.NARROWEST) != null;
	}

	/**
	 * Для оптимизации (чтобы не создавать новые экземпляры типов при простой проверке)
	 */
	protected boolean canCastToWidestImpl(Type other) {
		return this.castImpl(other, CastingKind.WIDEST) != null;
	}

	/**
	 * Для оптимизации (чтобы не создавать новые экземпляры типов при простой проверке)
	 */
	protected boolean canReversedCastToWidestImpl(Type other) {
		return this.reversedCastImpl(other, CastingKind.WIDEST) != null;
	}


	/**
	 * @return {@code true} если мы можем неявно преобразовать {@code this} в {@code other}. Например,
	 * {@code int} -> {@code long}. На уровне байткода мы не можем сделать такое преобразование неявно.
	 */
	public boolean canImplicitCastToNarrowest(Type other) {
		return canCastToNarrowest(other);
	}


	/**
	 * Реализация метода преобразования.
	 *
	 * @param other тип, к которому преобразуется {@code this}
	 * @param kind  вид преобразования
	 * @return Результат преобразования или {@code null}, если преобразование невозможно
	 */
	protected abstract @Nullable Type castImpl(Type other, CastingKind kind);


	/**
	 * Реализация метода преобразования к наиболее узкому типу.
	 * Вызывается, если метод {@link #castImpl(Type, CastingKind)} вернул {@code null}.
	 *
	 * @return Результат преобразования или {@code null}, если преобразование невозможно.
	 * Реализация по умолчанию возвращает {@code null}
	 */
	protected @Nullable Type reversedCastImpl(Type other, CastingKind kind) {
		return null;
	}


	/**
	 * Преобразует {@code this} к {@code other}
	 *
	 * @return Результат преобразования
	 * @throws IncompatibleTypesException если преобразование невозможно
	 */
	public final Type castTo(Type other, CastingKind kind) {
		Type type = castNoexcept(other, kind);

		if (type != null) return type;

		throw new IncompatibleTypesException(this, other, kind);
	}

	/**
	 * Преобразует тип к наиболее узкому типу (когда мы используем значение как значение какого-то типа)
	 *
	 * @return Результат преобразования
	 * @throws IncompatibleTypesException если преобразование невозможно
	 */
	public final Type castToNarrowest(Type other) {
		return castTo(other, CastingKind.NARROWEST);
	}

	/**
	 * Преобразует тип к наиболее широкому типу (используется при присвоении значения переменной)
	 *
	 * @return Результат преобразования
	 * @throws IncompatibleTypesException если преобразование невозможно
	 */
	public final Type castToWidest(Type other) {
		return castTo(other, CastingKind.WIDEST);
	}


	/**
	 * Преобразует {@code this} к {@code other}
	 *
	 * @return Результат преобразования или {@code null}, если преобразование невозможно
	 */
	public final @Nullable Type castNoexcept(Type other, CastingKind kind) {
		Type type = castImpl(other, kind);

		if (type != null) return type;

		return other.reversedCastImpl(this, kind);
	}

	/**
	 * Преобразует тип к наиболее узкому типу.
	 *
	 * @return Результат преобразования или {@code null}, если преобразование невозможно
	 */
	public final @Nullable Type castToNarrowestNoexcept(Type other) {
		return castNoexcept(other, CastingKind.NARROWEST);
	}

	/**
	 * Преобразует тип к наиболее широкому типу.
	 *
	 * @return Результат преобразования или {@code null}, если преобразование невозможно
	 */
	public final @Nullable Type castToWidestNoexcept(Type other) {
		return castNoexcept(other, CastingKind.WIDEST);
	}


	/**
	 * Преобразует тип к общему типу (используется, например, в тернарном операторе)
	 *
	 * @return Результат преобразования
	 * @throws IncompatibleTypesException если преобразование невозможно
	 */
	public final Type castToGeneral(Type other, GeneralCastingKind kind) {
		Type type = this.castToGeneralNoexcept(other, kind);

		if (type != null)
			return type;

		throw new IncompatibleTypesException(this, other, kind);
	}


	/**
	 * Преобразует тип к общему типу. Может мыть переопределён в подклассах
	 *
	 * @return Результат преобразования или {@code null}, если преобразование невозможно
	 */
	public @Nullable Type castToGeneralNoexcept(Type other, GeneralCastingKind kind) {
		Type type = this.castToWidestNoexcept(other);

		if (type != null)
			return type;

		return other.castToWidestNoexcept(this);
	}


	/**
	 * Преобразует тип к общему типу.
	 *
	 * @return Результат преобразования или {@code null}, если преобразование невозможно
	 */
	public @Nullable Type implicitCastToGeneralNoexcept(Type other, GeneralCastingKind kind) {
		Type type = castToGeneralNoexcept(other, kind);

		if (type != null)
			return type;

		if (this.canImplicitCastToNarrowest(other))
			return other;

		if (other.canImplicitCastToNarrowest(this))
			return this;

		return null;
	}


	/**
	 * @return Статус неявного преобразование к типу
	 * (для компилятора, т.е. мы можем конвертировать int в long в коде, но не можем сделать это на уровне байткода).
	 * Чем меньше статус, тем выше приоритет. Если статус больше или равен {@link CastStatus#NONE}, значит преобразование невозможно
	 */
	public int implicitCastStatus(Type other) {
		return this.equals(other) ? CastStatus.SAME :
				this.canImplicitCastToNarrowest(other) ? CastStatus.EXTEND : CastStatus.NONE;
	}


	/**
	 * Выполняет сведение типа.
	 */
	public abstract BasicType reduced();


	/**
	 * Заменяет каждое объявление дженерика на определение и возвращает получившийся тип.
	 * Этот метод объявлен в классе {@link Type} для корректной работы
	 * {@link ArrayType#replaceIndefiniteGenericsToDefinite(IClassInfo, GenericParameters)}.
	 */
	public Type replaceIndefiniteGenericsToDefinite(IClassInfo classinfo, GenericParameters<GenericDeclarationType> parameters) {
		return this;
	}

	/**
	 * Заменяет все wildcard типы на соответствующую границу и возвращает получившийся тип.
	 * Заменяет только тип верхнего уровня, т.е. для типа {@code List<?>} вернёт {@code List<?>},
	 * а для {@code ?} вернёт тот тип, от которого наследуется параметр класса.
	 * @param index индекс параметра в {@code parameters}
	 * @param parameters параметры, объявленные в классе
	 * @throws IndexOutOfBoundsException если индекс вне диапазона
	 */
	public Type replaceWildcardIndicatorsToBound(int index, GenericParameters<GenericDeclarationType> parameters) {
		return this;
	}

	/**
	 * Заменяет все generic параметры: каждый ключ заменяется на значение и возвращает получившийся тип.
	 * Этот метод объявлен в классе {@link Type} для корректной работы
	 * {@link ArrayType#replaceAllTypes(Map)}.
	 */
	public Type replaceAllTypes(@Immutable Map<GenericDeclarationType, ReferenceType> replaceTable) {
		return this;
	}


	@Override
	public final boolean equals(Object obj) {
		return this == obj || obj instanceof Type other && this.equals(other);
	}

	public final boolean equals(Type other) {
		return this == other ||
				this.getClass() == other.getClass() &&
						this.getEncodedName().equals(other.getEncodedName());
	}

	@Override
	public final int hashCode() {
		return getEncodedName().hashCode();
	}


	public final boolean equalsOneOf(Type other1, Type other2) {
		return this.equals(other1) || this.equals(other2);
	}

	public final boolean equalsOneOf(Type other1, Type other2, Type other3) {
		return this.equals(other1) || this.equals(other2) || this.equals(other3);
	}

	public final boolean equalsOneOf(Type other1, Type other2, Type other3, Type... others) {
		return equalsOneOf(other1, other2, other3) || Arrays.stream(others).anyMatch(this::equals);
	}

	/**
	 * Сравнивает типы без учёта сигнатуры
	 */
	public boolean equalsIgnoreSignature(Type other) {
		return this.equals(other);
	}


	public static Type fromClass(Class<?> clazz) {

		if (clazz.isPrimitive()) {
			if (clazz == byte.class) return PrimitiveType.BYTE;
			if (clazz == short.class) return PrimitiveType.SHORT;
			if (clazz == char.class) return PrimitiveType.CHAR;
			if (clazz == int.class) return PrimitiveType.INT;
			if (clazz == long.class) return PrimitiveType.LONG;
			if (clazz == float.class) return PrimitiveType.FLOAT;
			if (clazz == double.class) return PrimitiveType.DOUBLE;
			if (clazz == boolean.class) return PrimitiveType.BOOLEAN;
			if (clazz == void.class) return PrimitiveType.VOID;
			throw new IllegalArgumentException("Cannot recognize Class of primitive type \"" + clazz + "\"");
		}

		if (clazz.isArray()) {
			return ArrayType.fromClass(clazz);
		}

		return ClassType.fromClass(clazz);
	}


	public static Type fromReflectType(java.lang.reflect.Type reflectType) {
		if (reflectType instanceof Class<?> clazz) {
			return fromClass(clazz);
		}

		return ReferenceType.fromReflectType(reflectType);
	}


	/**
	 * @see #parseType(ExtendedStringInputStream)
	 */
	public static BasicType parseType(String str) {
		return parseType(new ExtendedStringInputStream(str));
	}

	/**
	 * Парсит любой тип, кроме {@link PrimitiveType#VOID}
	 */
	public static BasicType parseType(ExtendedStringInputStream in) {
		return switch (in.get()) {
			case 'B' -> {
				in.incPos();
				yield PrimitiveType.BYTE;
			}
			case 'C' -> {
				in.incPos();
				yield PrimitiveType.CHAR;
			}
			case 'S' -> {
				in.incPos();
				yield PrimitiveType.SHORT;
			}
			case 'I' -> {
				in.incPos();
				yield PrimitiveType.INT;
			}
			case 'J' -> {
				in.incPos();
				yield PrimitiveType.LONG;
			}
			case 'F' -> {
				in.incPos();
				yield PrimitiveType.FLOAT;
			}
			case 'D' -> {
				in.incPos();
				yield PrimitiveType.DOUBLE;
			}
			case 'Z' -> {
				in.incPos();
				yield PrimitiveType.BOOLEAN;
			}
			case 'L' -> ClassType.read(in.next());
			case '[' -> ArrayType.read(in);
			case 'T' -> NamedGenericType.read(in.next());
			default -> throw new InvalidTypeNameException(in, in.distanceToMark());
		};
	}


	/**
	 * @see #parseMethodArguments(ExtendedStringInputStream)
	 */
	public static @Immutable List<Type> parseMethodArguments(String str) {
		return parseMethodArguments(new ExtendedStringInputStream(str));
	}

	/**
	 * Парсит сигнатуру метода, возвращает список аргументов
	 */
	public static @Immutable List<Type> parseMethodArguments(ExtendedStringInputStream in) {
		in.mark();

		if (in.read() != '(')
			throw new InvalidMethodDescriptorException(in);

		List<Type> arguments = new ArrayList<>();

		while (true) {
			if (in.get() == ')') {
				in.incPos();
				in.unmark();
				return Collections.unmodifiableList(arguments);
			}

			arguments.add(parseType(in));
		}
	}


	/**
	 * Парсит возвращаемый тип метода, который может быть {@link PrimitiveType#VOID}
	 */
	public static BasicType parseReturnType(ExtendedStringInputStream in) {
		if (in.get() == 'V') {
			in.incPos();
			return PrimitiveType.VOID;
		}

		return parseType(in);
	}


	/**
	 * Парсит тип массива или класса (без префикса 'L')
	 */
	public static RealReferenceType parseRealReferenceType(String encodedName) {
		return encodedName.charAt(0) == '[' ?
				ArrayType.fromDescriptor(encodedName) :
				ClassType.fromDescriptor(encodedName);
	}


	/**
	 * Парсит тип массива, класса или параметра
	 */
	public static ReferenceType parseSignatureParameter(ExtendedStringInputStream in) {
		return switch (in.get()) {
			case 'L' -> ClassType.read(in.next());
			case '[' -> ArrayType.read(in);
			case 'T' -> NamedGenericType.read(in.next());
			default -> throw new InvalidTypeNameException(in, in.distanceToMark());
		};
	}


	private static final Function<ExtendedStringInputStream, ReferenceType> signatureParameterGetter =
			in -> switch (in.get()) {
				case '+' -> new ExtendingGenericType(in.next());
				case '-' -> new SuperGenericType(in.next());
				case '*' -> {
					in.incPos();
					yield AnyGenericType.INSTANCE;
				}

				case UncheckedInputStream.EOF_CHAR -> throw new InvalidTypeNameException(in, in.distanceToMark());

				default -> parseSignatureParameter(in);
			};


	public static GenericParameters<ReferenceType> parseSignature(ExtendedStringInputStream in) {
		return GenericParameters.readNonempty(in, signatureParameterGetter);
	}

	public static GenericParameters<ReferenceType> parseEmptyableSignature(ExtendedStringInputStream in) {
		return GenericParameters.readEmptyable(in, signatureParameterGetter);
	}

	public static GenericParameters<GenericDeclarationType> parseEmptyableGenericParameters(ExtendedStringInputStream in) {
		return GenericParameters.readEmptyable(in, GenericDeclarationType::read);
	}
}
