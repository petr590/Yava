package x590.yava.attribute.code;

import x590.util.annotation.Immutable;
import x590.yava.attribute.Attribute;
import x590.yava.attribute.Sizes;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.serializable.JavaSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LineNumberTableAttribute extends Attribute {

	private final @Immutable List<LineNumberEntry> table;

	public LineNumberTableAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);

		this.table = in.readImmutableList(() -> new LineNumberEntry(in));
	}

	public LineNumberTableAttribute(String name, AssemblingInputStream in, ConstantPool pool) {
		super(name);

		List<LineNumberEntry> table = new ArrayList<>();

		in.requireNext('{');

		while (!in.advanceIfHasNext('}')) {
			table.add(new LineNumberEntry(in, pool));
		}

		this.table = Collections.unmodifiableList(table);

		initLength(Sizes.LENGTH + table.size() * LineNumberEntry.SIZE);
	}

	public static final class LineNumberEntry implements JavaSerializable {

		public static final int SIZE = Sizes.SHORT * 2;

		private final int startPos, lineNumber;

		public LineNumberEntry(ExtendedDataInputStream in) {
			this.startPos = in.readUnsignedShort();
			this.lineNumber = in.readUnsignedShort();
		}

		public LineNumberEntry(AssemblingInputStream in, ConstantPool pool) {
			this.lineNumber = in.requireNext("line").nextUnsignedShort();
			this.startPos = in.requireNext(':').nextUnsignedShort();
		}

		public int getStartPos() {
			return startPos;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		@Override
		public void serialize(AssemblingOutputStream out) {
			out.recordShort(startPos).recordShort(lineNumber);
		}
	}

	protected void serializeData(AssemblingOutputStream out, ConstantPool pool) {
		out.writeAll(table);
	}
}
