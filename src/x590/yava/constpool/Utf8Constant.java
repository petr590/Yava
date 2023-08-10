package x590.yava.constpool;

import x590.yava.exception.disassembling.DisassemblingException;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.io.AssemblingOutputStream;

public final class Utf8Constant extends Constant {

	private final String value;

	public Utf8Constant(String value) {
		this.value = value;
	}

	public String getString() {
		return value;
	}

	static String decodeUtf8(ExtendedDataInputStream in) {
		int length = in.readUnsignedShort();
		StringBuilder result = new StringBuilder();

		int i = 0;

		for (; i < length; i++) {
			int ch = in.readUnsignedByte();

			if ((ch & 0xE0) == 0xC0) {
				ch = (ch & 0x1F) << 6 | (in.readByte() & 0x3F);
				i += 1;

			} else if ((ch & 0xF0) == 0xE0) {

				int b1 = in.readByte(),
						b2 = in.readByte();

				if (ch == 0xED && i + 5 < length &&
						(b1 & 0xF0) == 0xA0 && (b2 & 0xC0) == 0x80) {

					int b3 = in.readByte(),
							b4 = in.readByte(),
							b5 = in.readByte();

					if ((b3 & 0xFF) == 0xED
							&& (b4 & 0xF0) == 0xB0 && (b5 & 0xC0) == 0x80) {

						int c = 0x10000 | (b1 & 0xF) << 16 |
								(b2 & 0x3F) << 10 | (b4 & 0xF) << 6 | (b5 & 0x3F);

						if ((c & 0xFFFF0000) != 0)
							result.append((char)(c >>> 16));
						result.append((char)c);

						i += 5;
						continue;
					}
				}

				ch = (ch & 0xF) << 12 | (b1 & 0x3F) << 6 | (b2 & 0x3F);

				i += 2;
			}

			result.append((char)ch);
		}

		if (i != length)
			throw new DisassemblingException("String decoding failed");

		return result.toString();
	}

	@Override
	public String getConstantName() {
		return UTF8;
	}

	@Override
	public String toString() {
		return String.format("Utf8Constant \"%s\"", value);
	}

	@Override
	public void serialize(AssemblingOutputStream out) {
		out.recordByte(TAG_UTF8).writeByteArraySized(value.getBytes());
	}


	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof Utf8Constant constant && this.equals(constant);
	}

	public boolean equals(Utf8Constant other) {
		return this == other || value.equals(other.value);
	}
}
