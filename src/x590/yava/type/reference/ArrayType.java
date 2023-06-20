package x590.yava.type.reference;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import x590.util.Pair;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.clazz.ClassInfo;
import x590.yava.clazz.IClassInfo;
import x590.yava.exception.disassembling.InvalidArrayNameException;
import x590.yava.io.ExtendedOutputStream;
import x590.yava.io.ExtendedStringInputStream;
import x590.yava.io.StringifyOutputStream;
import x590.yava.main.Yava;
import x590.yava.type.BasicType;
import x590.yava.type.CastingKind;
import x590.yava.type.Type;
import x590.yava.type.Types;
import x590.yava.type.primitive.PrimitiveType;
import x590.yava.type.reference.generic.GenericDeclarationType;
import x590.yava.type.reference.generic.GenericParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Описывает тип массива, включая массив примитивов,
 * многомерные массивы и массивы SpecialType
 */
public final class ArrayType extends RealReferenceType implements IArrayType {

	private static final Map<Type, Int2ObjectMap<ArrayType>> INSTANCES = new HashMap<>();

	private static final ClassType ARRAY_SUPER_TYPE = ClassType.OBJECT;

	private static final @Immutable List<ClassType> ARRAY_INTERFACES =
			List.of(ClassType.CLONEABLE, ClassType.SERIALIZABLE);


	public static final ArrayType
			ANY_ARRAY = init(Types.ANY_TYPE),
			ANY_OBJECT_ARRAY = init(Types.ANY_OBJECT_TYPE),

	OBJECT_ARRAY = init(ClassType.OBJECT),
			STRING_ARRAY = init(ClassType.STRING),

	BYTE_OR_BOOLEAN_ARRAY = init(PrimitiveType.BYTE_BOOLEAN),
			BOOLEAN_ARRAY = init(PrimitiveType.BOOLEAN),
			BYTE_ARRAY = init(PrimitiveType.BYTE),
			SHORT_ARRAY = init(PrimitiveType.SHORT),
			CHAR_ARRAY = init(PrimitiveType.CHAR),
			INT_ARRAY = init(PrimitiveType.INT),
			LONG_ARRAY = init(PrimitiveType.LONG),
			FLOAT_ARRAY = init(PrimitiveType.FLOAT),
			DOUBLE_ARRAY = init(PrimitiveType.DOUBLE);


	private static ArrayType init(Type memberType) {
		var arrayType = new ArrayType(memberType, 1);

		var prev = INSTANCES
				.computeIfAbsent(memberType, membType -> new Int2ObjectArrayMap<>())
				.put(1, arrayType);

		assert prev == null;

		return arrayType;
	}


	public static ArrayType fromClass(Class<?> clazz) {

		Class<?> componentClass = clazz.getComponentType();

		if (componentClass == null) {
			throw new IllegalArgumentException("Class " + clazz.getName() + " is not an array");
		}

		if (componentClass.isPrimitive()) {
			if (componentClass == byte.class) return BYTE_ARRAY;
			if (componentClass == short.class) return SHORT_ARRAY;
			if (componentClass == char.class) return CHAR_ARRAY;
			if (componentClass == int.class) return INT_ARRAY;
			if (componentClass == long.class) return LONG_ARRAY;
			if (componentClass == float.class) return FLOAT_ARRAY;
			if (componentClass == double.class) return DOUBLE_ARRAY;
			if (componentClass == boolean.class) return BOOLEAN_ARRAY;
			if (componentClass == void.class) throw new IllegalArgumentException("Illegal type: array of voids");
			throw new IllegalArgumentException("Cannot recognize Class of primitive type \"" + componentClass + "\"");
		}

		int nestingLevel = 1;

		while (componentClass.isArray()) {
			componentClass = componentClass.getComponentType();
			nestingLevel += 1;
		}

		return new ArrayType(Type.fromClass(componentClass), nestingLevel);
	}


	public static ArrayType fromDescriptor(String arrayEncodedName) {
		return read(new ExtendedStringInputStream(arrayEncodedName));
	}

	public static ArrayType read(ExtendedStringInputStream in) {

		in.mark();

		int nestingLevel = 0;

		for (int ch = in.read(); ch == '['; ch = in.read()) {
			nestingLevel++;
		}

		if (nestingLevel == 0) {
			throw new InvalidArrayNameException(in);
		}

		in.prev();

		Type memberType = parseType(in);

		in.unmark();

		return INSTANCES
				.computeIfAbsent(memberType, key -> new Int2ObjectArrayMap<>())
				.computeIfAbsent(nestingLevel,
						nestLevel -> new ArrayType(memberType, nestLevel));
	}


	public static ArrayType forType(Type memberType, int nestingLevel) {

		if (nestingLevel <= 0) {
			throw new IllegalArgumentException("nestingLevel <= 0");
		}

		final Type finalMemberType;

		if (memberType instanceof ArrayType arrayMemberType) {
			nestingLevel += arrayMemberType.nestingLevel;
			finalMemberType = arrayMemberType.memberType;
		} else {
			finalMemberType = memberType;
		}

		return INSTANCES
				.computeIfAbsent(memberType, key -> new Int2ObjectArrayMap<>())
				.computeIfAbsent(nestingLevel,
						nestLevel -> new ArrayType(finalMemberType, nestLevel));
	}


	private final Type memberType, elementType;

	private final int nestingLevel;

	private final String
			braces,
			encodedName,
			classEncodedName,
			name;
	private final @Nullable String binaryName;

	private @Nullable String nameForVariable;


	private ArrayType(Type memberType, int nestingLevel) {
		super(ARRAY_SUPER_TYPE, ARRAY_INTERFACES);

		this.nestingLevel = nestingLevel;

		this.memberType = memberType;

		this.elementType = nestingLevel == 1 ? memberType : forType(memberType, nestingLevel - 1);

		this.braces = "[]".repeat(nestingLevel);
		String encodedBraces = "[".repeat(nestingLevel);

		this.name = memberType.getName() + braces;

		this.binaryName =
				(memberType instanceof ClassType classType ?
						encodedBraces + 'L' + classType.getBinaryName() + ';' :
						memberType.getBinaryName() == null ?
								null :
								encodedBraces + memberType.getBinaryName());

		this.encodedName = encodedBraces + memberType.getEncodedName();

		this.classEncodedName = encodedBraces +
				(memberType instanceof RealReferenceType realReference ?
						realReference.getClassEncodedName() :
						memberType.getEncodedName());
	}


	@Override
	public Type getMemberType() {
		return memberType;
	}

	@Override
	public Type getElementType() {
		return elementType;
	}

	@Override
	public int getNestingLevel() {
		return nestingLevel;
	}

	@Override
	public Type getArrayMemberIfUsingCArrays() {
		return Yava.getConfig().useCStyleArray() ? memberType : this;
	}


	@Override
	public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
		out.printObject(memberType, classinfo).print(braces);
	}

	@Override
	public void writeLeftDefinition(StringifyOutputStream out, ClassInfo classinfo) {
		out.print(Yava.getConfig().useCStyleArray() ? memberType : this, classinfo);
	}

	@Override
	public void writeRightDefinition(StringifyOutputStream out, ClassInfo classinfo) {
		if (Yava.getConfig().useCStyleArray())
			out.write(braces);
	}

	@Override
	public String toString() {
		return memberType.toString() + braces;
	}

	@Override
	public String getClassEncodedName() {
		return classEncodedName;
	}

	@Override
	public String getEncodedName() {
		return encodedName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getBinaryName() {
		return binaryName;
	}

	@Override
	public String getNameForVariable() {
		if (nameForVariable != null)
			return nameForVariable;

		Type memberType = this.memberType;

		StringBuilder nameBuilder = new StringBuilder(memberType.isPrimitive() ? memberType.getName() : memberType.getNameForVariable());

		if (nestingLevel != 1) {
			if (Character.isDigit(nameBuilder.charAt(nameBuilder.length() - 1)))
				nameBuilder.append('_');

			nameBuilder.append(nestingLevel).append('d');
		}

		return nameForVariable = nameBuilder.append("Array").toString();
	}

	@Override
	public boolean isGenericType() {
		return memberType.isGenericType();
	}


	@Override
	public void addImports(ClassInfo classinfo) {
		classinfo.addImport(memberType);
	}


	@Override
	protected Pair<ClassType, List<ClassType>> tryLoadSuperTypes() {
		return Pair.empty();
	}


	@Override
	protected boolean canCastToNarrowestImpl(Type other) {
		if (other.equalsIgnoreSignature(ARRAY_SUPER_TYPE) ||
				ARRAY_INTERFACES.stream().anyMatch(other::equalsIgnoreSignature)) {

			return true;
		}

		if (other instanceof ArrayType arrayType) {
			return nestingLevel == arrayType.nestingLevel && memberType.canCastToNarrowest(arrayType.memberType)
					|| elementType.canCastToNarrowest(arrayType.elementType);
		}

		return false;
	}

	@Override
	protected boolean canCastToWidestImpl(Type other) {
		if (other instanceof ArrayType arrayType) {
			return nestingLevel == arrayType.nestingLevel && memberType.canCastToWidest(arrayType.memberType)
					|| elementType.canCastToWidest(arrayType.elementType);
		}

		return false;
	}

	@Override
	protected Type castImpl(Type other, CastingKind kind) {
		if (other.equalsIgnoreSignature(ARRAY_SUPER_TYPE) ||
				ARRAY_INTERFACES.stream().anyMatch(other::equalsIgnoreSignature)) {

			return this;
		}

		if (other instanceof ArrayType arrayType) {

			int nestingLevel = this.nestingLevel;
			Type memberType, thisMemberType;

			if (nestingLevel == arrayType.nestingLevel) {
				thisMemberType = this.memberType;
				memberType = thisMemberType.castNoexcept(arrayType.memberType, kind);

			} else {
				thisMemberType = this.elementType;
				memberType = thisMemberType.castNoexcept(arrayType.elementType, kind);
				nestingLevel = 1;
			}

			return memberType == null ? null :
					memberType.equals(thisMemberType) ? this :
							memberType.arrayType(nestingLevel).asType();
		}

		return null;
	}


	@Override
	public boolean isDefinitely(int modifiers) {
		return memberType instanceof ReferenceType referenceType && referenceType.isDefinitely(modifiers);
	}

	@Override
	public boolean isDefinitelyNot(int modifiers) {
		return memberType instanceof ReferenceType referenceType && referenceType.isDefinitelyNot(modifiers);
	}


	@Override
	public boolean isDefinitelySubclassOf(ReferenceType other) {
		if (super.isDefinitelySubclassOf(other)) {
			return true;
		}

		if (other instanceof ArrayType otherArray) {
			return nestingLevel == otherArray.nestingLevel ?
					memberType.isDefinitelySubtypeOf(otherArray.memberType) :
					elementType.isDefinitelySubtypeOf(otherArray.elementType);
		}

		return false;
	}


	@Override
	public BasicType reduced() {
		var memberType = this.memberType;
		var reducedMemberType = memberType.reduced();
		return reducedMemberType == memberType ? this : ArrayType.forType(reducedMemberType, nestingLevel);
	}

	@Override
	public ReferenceType replaceUndefiniteGenericsToDefinite(IClassInfo classinfo, GenericParameters<GenericDeclarationType> parameters) {
		return replaceMemberType(memberType -> memberType.replaceUndefiniteGenericsToDefinite(classinfo, parameters));
	}

	@Override
	public ReferenceType replaceAllTypes(@Immutable Map<GenericDeclarationType, ReferenceType> replaceTable) {
		return replaceMemberType(memberType -> memberType.replaceAllTypes(replaceTable));
	}

	private ReferenceType replaceMemberType(Function<? super Type, ? extends Type> replacer) {
		var memberType = this.memberType;
		var replacedMemberType = replacer.apply(memberType);
		return replacedMemberType == memberType ? this : ArrayType.forType(replacedMemberType, nestingLevel);
	}


	@Override
	public boolean equalsIgnoreSignature(Type other) {
		return this == other || other instanceof ArrayType arrayType && this.equalsIgnoreSignature(arrayType);
	}

	public boolean equalsIgnoreSignature(ArrayType other) {
		return this == other ||
				(nestingLevel == other.nestingLevel ?
						memberType.equalsIgnoreSignature(other.memberType) :
						elementType.equalsIgnoreSignature(other.elementType));
	}


	public static int minNestingLevel(ArrayType arrayType1, ArrayType arrayType2) {
		return Math.min(arrayType1.nestingLevel, arrayType2.nestingLevel);
	}
}
