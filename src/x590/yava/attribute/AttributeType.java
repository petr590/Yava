package x590.yava.attribute;

import x590.util.annotation.Immutable;
import x590.yava.attribute.Attributes.Location;
import x590.yava.attribute.annotation.AnnotationDefaultAttribute;
import x590.yava.attribute.annotation.AnnotationsAttribute;
import x590.yava.attribute.annotation.ParameterAnnotationsAttribute;
import x590.yava.attribute.annotation.TypeAnnotationsAttribute;
import x590.yava.attribute.code.CodeAttribute;
import x590.yava.attribute.code.LineNumberTableAttribute;
import x590.yava.attribute.code.LocalVariableTableAttribute;
import x590.yava.attribute.signature.ClassSignatureAttribute;
import x590.yava.attribute.signature.FieldSignatureAttribute;
import x590.yava.attribute.signature.MethodSignatureAttribute;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.parsing.ParseException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;

import java.util.*;

import static x590.yava.attribute.AttributeReader.reader;
import static x590.yava.attribute.AttributeParser.parser;
import static x590.yava.attribute.AttributeParser.simpleParser;

public class AttributeType<A extends Attribute> implements AttributeReader<A>, AttributeParser<A> {

	private static final Map<Location, Map<String, AttributeType<?>>> ATTRIBUTE_TYPES = new EnumMap<>(Location.class);

	private static final @Immutable Set<Location> CLASS_FIELD_OR_METHOD_LOCATION = EnumSet.of(Location.CLASS, Location.FIELD, Location.METHOD);
	private static final @Immutable Set<Location> CLASS_FIELD_METHOD_OR_CODE_LOCATION = EnumSet.of(Location.CLASS, Location.FIELD, Location.METHOD, Location.CODE_ATTRIBUTE);

	// Временно
	private static final AttributeParser<?> unhandledParser = (name, in, pool, location) -> {
		throw new ParseException("Attribute \"" + name + "\" yet not finished");
	};


	public static final AttributeType<UnknownAttribute> UNKNOWN =
			new AttributeType<>("",
					(name, length, in, pool, location) -> new UnknownAttribute(name, length, in),
					(name, in, pool, location) -> {
						throw new ParseException("Unknown attribute \"" + name + "\"");
					}
			);


	// Class
	public static final AttributeType<SourceFileAttribute> SOURCE_FILE =
			create(Location.CLASS, AttributeNames.SOURCE_FILE, reader(SourceFileAttribute::new));

	public static final AttributeType<ClassSignatureAttribute> CLASS_SIGNATURE =
			create(Location.CLASS, AttributeNames.SIGNATURE, reader(ClassSignatureAttribute::new));

	public static final EmptyableAttributeType<InnerClassesAttribute> INNER_CLASSES =
			create(Location.CLASS, AttributeNames.INNER_CLASSES, reader(InnerClassesAttribute::new), InnerClassesAttribute.empty());

//	public static final AttributeType<EnclosingMethodAttribute> ENCLOSING_METHOD = create(Location.CLASS, AttributeNames.ENCLOSING_METHOD, EnclosingMethodAttribute::new);

	public static final AttributeType<BootstrapMethodsAttribute> BOOTSTRAP_METHODS =
			create(Location.CLASS, AttributeNames.BOOTSTRAP_METHODS, reader(BootstrapMethodsAttribute::new));

	public static final AttributeType<ModuleAttribute> MODULE =
			create(Location.CLASS, AttributeNames.MODULE, reader(ModuleAttribute::new));

	public static final AttributeType<PermittedSubclassesAttribute> PERMITTED_SUBCLASSES =
			create(Location.CLASS, AttributeNames.PERMITTED_SUBCLASSES, reader(PermittedSubclassesAttribute::new));


	// Field
	public static final AttributeType<ConstantValueAttribute> CONSTANT_VALUE =
			create(Location.FIELD, AttributeNames.CONSTANT_VALUE, reader(ConstantValueAttribute::new), parser(ConstantValueAttribute::new));

	public static final AttributeType<FieldSignatureAttribute> FIELD_SIGNATURE =
			create(Location.FIELD, AttributeNames.SIGNATURE, reader(FieldSignatureAttribute::new));


	// Method
	public static final EmptyableAttributeType<CodeAttribute> CODE =
			create(Location.METHOD, AttributeNames.CODE, reader(CodeAttribute::new), parser(CodeAttribute::new), CodeAttribute.empty());

	public static final EmptyableAttributeType<ExceptionsAttribute> EXCEPTIONS =
			create(Location.METHOD, AttributeNames.EXCEPTIONS, reader(ExceptionsAttribute::new), parser(ExceptionsAttribute::new), ExceptionsAttribute.empty());

	public static final AttributeType<MethodSignatureAttribute> METHOD_SIGNATURE =
			create(Location.METHOD, AttributeNames.SIGNATURE, reader(MethodSignatureAttribute::new), parser(MethodSignatureAttribute::new));

	public static final AttributeType<AnnotationDefaultAttribute> ANNOTATION_DEFAULT =
			create(Location.METHOD, AttributeNames.ANNOTATION_DEFAULT, reader(AnnotationDefaultAttribute::new));

	public static final EmptyableAttributeType<ParameterAnnotationsAttribute> RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS =
			create(Location.METHOD, AttributeNames.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS, reader(ParameterAnnotationsAttribute::new), ParameterAnnotationsAttribute.emptyVisible());

	public static final EmptyableAttributeType<ParameterAnnotationsAttribute> RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS =
			create(Location.METHOD, AttributeNames.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS, reader(ParameterAnnotationsAttribute::new), ParameterAnnotationsAttribute.emptyInvisible());


	// Code
	public static final AttributeType<LineNumberTableAttribute> LINE_NUMBER_TABLE =
			create(Location.CODE_ATTRIBUTE, AttributeNames.LINE_NUMBER_TABLE, reader(LineNumberTableAttribute::new), parser(LineNumberTableAttribute::new));

	public static final EmptyableAttributeType<LocalVariableTableAttribute> LOCAL_VARIABLE_TABLE =
			create(Location.CODE_ATTRIBUTE, AttributeNames.LOCAL_VARIABLE_TABLE, reader(LocalVariableTableAttribute::new), parser(LocalVariableTableAttribute::new), LocalVariableTableAttribute.emptyTable());

	public static final EmptyableAttributeType<LocalVariableTableAttribute> LOCAL_VARIABLE_TYPE_TABLE =
			create(Location.CODE_ATTRIBUTE, AttributeNames.LOCAL_VARIABLE_TYPE_TABLE, reader(LocalVariableTableAttribute::new), parser(LocalVariableTableAttribute::new), LocalVariableTableAttribute.emptyTypeTable());


	// Class, field, method
	public static final AttributeType<SyntheticAttribute> SYNTHETIC =
			create(CLASS_FIELD_OR_METHOD_LOCATION, AttributeNames.SYNTHETIC, reader(SyntheticAttribute::get), simpleParser(SyntheticAttribute::get));

	public static final AttributeType<DeprecatedAttribute> DEPRECATED =
			create(CLASS_FIELD_OR_METHOD_LOCATION, AttributeNames.DEPRECATED, reader(DeprecatedAttribute::get), simpleParser(DeprecatedAttribute::get));

	public static final EmptyableAttributeType<AnnotationsAttribute> RUNTIME_VISIBLE_ANNOTATIONS =
			create(CLASS_FIELD_OR_METHOD_LOCATION, AttributeNames.RUNTIME_VISIBLE_ANNOTATIONS, reader(AnnotationsAttribute::new), AnnotationsAttribute.emptyVisible());

	public static final EmptyableAttributeType<AnnotationsAttribute> RUNTIME_INVISIBLE_ANNOTATIONS =
			create(CLASS_FIELD_OR_METHOD_LOCATION, AttributeNames.RUNTIME_INVISIBLE_ANNOTATIONS, reader(AnnotationsAttribute::new), AnnotationsAttribute.emptyInvisible());


	// Class, field, method, code
	public static final AttributeType<TypeAnnotationsAttribute> RUNTIME_VISIBLE_TYPE_ANNOTATIONS =
			create(CLASS_FIELD_METHOD_OR_CODE_LOCATION, AttributeNames.RUNTIME_VISIBLE_TYPE_ANNOTATIONS, TypeAnnotationsAttribute::new);

	public static final AttributeType<TypeAnnotationsAttribute> RUNTIME_INVISIBLE_TYPE_ANNOTATIONS =
			create(CLASS_FIELD_METHOD_OR_CODE_LOCATION, AttributeNames.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS, TypeAnnotationsAttribute::new);


	private final String name;
	private final AttributeReader<A> reader;
	private final AttributeParser<A> parser;

	private void putToMap(Location location) {
		ATTRIBUTE_TYPES.computeIfAbsent(location, loc -> new HashMap<>()).put(name, this);
	}

	private AttributeType(String name, AttributeReader<A> reader, AttributeParser<A> parser) {
		this.name = Objects.requireNonNull(name);
		this.reader = Objects.requireNonNull(reader);
		this.parser = Objects.requireNonNull(parser);
	}

	protected AttributeType(Location location, String name, AttributeReader<A> reader, AttributeParser<A> parser) {
		this(name, reader, parser);
		putToMap(location);
	}

	protected AttributeType(Set<Location> locations, String name, AttributeReader<A> reader, AttributeParser<A> parser) {
		this(name, reader, parser);

		for (Location location : locations)
			putToMap(location);
	}


	@Deprecated
	@SuppressWarnings("unchecked")
	public static <A extends Attribute> AttributeType<A> create(Location location, String name, AttributeReader<A> reader) {
		return new AttributeType<>(location, name, reader, (AttributeParser<A>) unhandledParser);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static <A extends Attribute> AttributeType<A> create(Set<Location> locations, String name, AttributeReader<A> reader) {
		return new AttributeType<>(locations, name, reader, (AttributeParser<A>) unhandledParser);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static <A extends Attribute> EmptyableAttributeType<A> create(Location location, String name, AttributeReader<A> reader, A emptyAttribute) {
		return new EmptyableAttributeType<>(location, name, reader, (AttributeParser<A>) unhandledParser, emptyAttribute);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static <A extends Attribute> EmptyableAttributeType<A> create(Set<Location> locations, String name, AttributeReader<A> reader, A emptyAttribute) {
		return new EmptyableAttributeType<>(locations, name, reader, (AttributeParser<A>) unhandledParser, emptyAttribute);
	}


	public static <A extends Attribute> AttributeType<A> create(Location location, String name, AttributeReader<A> reader, AttributeParser<A> parser) {
		return new AttributeType<>(location, name, reader, parser);
	}

	public static <A extends Attribute> AttributeType<A> create(Set<Location> locations, String name, AttributeReader<A> reader, AttributeParser<A> parser) {
		return new AttributeType<>(locations, name, reader, parser);
	}

	public static <A extends Attribute> EmptyableAttributeType<A> create(Location location, String name, AttributeReader<A> reader, AttributeParser<A> parser, A emptyAttribute) {
		return new EmptyableAttributeType<>(location, name, reader, parser, emptyAttribute);
	}

	public static <A extends Attribute> EmptyableAttributeType<A> create(Set<Location> locations, String name, AttributeReader<A> reader, AttributeParser<A> parser, A emptyAttribute) {
		return new EmptyableAttributeType<>(locations, name, reader, parser, emptyAttribute);
	}


	public String getName() {
		return name;
	}

	@Override
	public A readAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool, Location location) {
		return reader.readAttribute(name, length, in, pool, location);
	}

	public A parseAttribute(String name, AssemblingInputStream in, ConstantPool pool, Location location) {
		return parser.parseAttribute(name, in, pool, location);
	}


	public static AttributeType<?> getAttributeType(Location location, String name) {
		return ATTRIBUTE_TYPES.getOrDefault(location, Collections.emptyMap()).getOrDefault(name, UNKNOWN);
	}
}
