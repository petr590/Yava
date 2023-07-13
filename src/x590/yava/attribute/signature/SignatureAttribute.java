package x590.yava.attribute.signature;

import x590.yava.attribute.Attribute;

public abstract class SignatureAttribute extends Attribute {

	public SignatureAttribute(String name, int length) {
		super(name, length);
	}

	public SignatureAttribute(String name) {
		super(name);
	}
}
