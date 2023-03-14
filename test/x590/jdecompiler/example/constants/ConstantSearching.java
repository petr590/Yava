package x590.jdecompiler.example.constants;

import x590.jdecompiler.example.ExampleTesting;
import x590.jdecompiler.example.annotation.MultiAnnotation;

@MultiAnnotation(
		booleanValue = ConstantSearching.BOOLEAN_CONSTANT,
		byteValue = ConstantSearching.BYTE_CONSTANT,
		shortValue = ConstantSearching.SHORT_CONSTANT,
		charValue = ConstantSearching.CHAR_CONSTANT,
		intValue = ConstantSearching.INT_CONSTANT
)
public class ConstantSearching {
	
	public static void main(String[] args) {
		ExampleTesting.runDecompiler(ConstantSearching.class);
	}
	
	public static final boolean BOOLEAN_CONSTANT = true;
	public static final byte BYTE_CONSTANT = 1;
	public static final short SHORT_CONSTANT = 1;
	public static final char CHAR_CONSTANT = 1;
	public static final int INT_CONSTANT = 1;
	
	@SuppressWarnings("unused")
	@MultiAnnotation(
			booleanValue = BOOLEAN_CONSTANT,
			byteValue = BYTE_CONSTANT,
			shortValue = SHORT_CONSTANT,
			charValue = CHAR_CONSTANT,
			intValue = INT_CONSTANT
	)
	public static void foo() {
		Boolean z = BOOLEAN_CONSTANT;
		Byte b = BYTE_CONSTANT;
		Short s = SHORT_CONSTANT;
		Character c = CHAR_CONSTANT;
		Integer i = INT_CONSTANT;
		
		System.out.println(BOOLEAN_CONSTANT);
		System.out.println(BYTE_CONSTANT);
		System.out.println(SHORT_CONSTANT);
		System.out.println(CHAR_CONSTANT);
		System.out.println(INT_CONSTANT);
	}
}