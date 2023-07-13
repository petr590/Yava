package x590.yava.attribute;

import x590.yava.attribute.Attributes.Location;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.AssemblingInputStream;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface AttributeParser<A extends Attribute> {

	A parseAttribute(String name, AssemblingInputStream in, ConstantPool pool, Location location);

	static <A extends Attribute> AttributeParser<A> parser(AttributeParserIgnoringLocation<A> parser) {
		return parser;
	}

	static <A extends Attribute> AttributeParser<A> simpleParser(Function<AssemblingInputStream, A> function) {
		return (name, in, pool, location) -> function.apply(in);
	}

	@FunctionalInterface
	interface AttributeParserIgnoringLocation<A extends Attribute> extends AttributeParser<A> {
		default A parseAttribute(String name, AssemblingInputStream in, ConstantPool pool, Location location) {
			return parseAttribute(name, in, pool);
		}

		A parseAttribute(String name, AssemblingInputStream in, ConstantPool pool);
	}

	@FunctionalInterface
	interface AttributeParserWithInputStream<A extends Attribute> extends AttributeParser<A> {
		default A parseAttribute(String name, AssemblingInputStream in, ConstantPool pool, Location location) {
			return parseAttribute(in);
		}

		A parseAttribute(AssemblingInputStream in);
	}
}
