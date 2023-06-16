package x590.yava.attribute;

import x590.yava.attribute.Attributes.Location;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.ExtendedDataInputStream;
import x590.util.function.ObjIntFunction;

@FunctionalInterface
public interface AttributeReader<A extends Attribute> {
	
	A readAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool, Location location);
	
	static <A extends Attribute> AttributeReader<A> reader(ObjIntFunction<String, A> reader) {
		return (name, length, in, pool, location) -> reader.apply(name, length);
	}

	static <A extends Attribute> AttributeReader<A> reader(AttributeReaderIgnoringLocation<A> reader) {
		return reader;
	}
	
	@FunctionalInterface
	interface AttributeReaderIgnoringLocation<A extends Attribute> extends AttributeReader<A> {
		
		A readAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool);
		
		@Override
		default A readAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool, Location location) {
			return readAttribute(name, length, in, pool);
		}
	}
}
