package x590.yava.attribute;

import java.util.Set;

import x590.yava.attribute.Attributes.Location;

/**
 * Содержит пустой атрибут
 */
public class EmptyableAttributeType<A extends Attribute> extends AttributeType<A> {
	
	private final A emptyAttribute;
	
	protected EmptyableAttributeType(Location location, String name, AttributeReader<A> reader, AttributeParser<A> parser, A emptyAttribute) {
		super(location, name, reader, parser);
		this.emptyAttribute = emptyAttribute;
	}
	
	protected EmptyableAttributeType(Set<Location> locations, String name, AttributeReader<A> reader, AttributeParser<A> parser, A emptyAttribute) {
		super(locations, name, reader, parser);
		this.emptyAttribute = emptyAttribute;
	}
	
	public A getEmptyAttribute() {
		return emptyAttribute;
	}
}
