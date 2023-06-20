package x590.yava.attribute;

import x590.util.annotation.Immutable;
import x590.yava.constpool.ConstantPool;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class LocalVariableTableAttribute extends Attribute {

	private static final LocalVariableTableAttribute
			EMPTY_TABLE = new LocalVariableTableAttribute(AttributeNames.LOCAL_VARIABLE_TABLE),
			EMPTY_TYPE_TABLE = new LocalVariableTableAttribute(AttributeNames.LOCAL_VARIABLE_TYPE_TABLE);

	private final @Immutable List<LocalVariableEntry> table;

	private LocalVariableTableAttribute(String name) {
		super(name, 0);
		this.table = Collections.emptyList();
	}

	LocalVariableTableAttribute(String name, int length, ExtendedDataInputStream in, ConstantPool pool) {
		super(name, length);

		this.table = in.readImmutableList(() -> new LocalVariableEntry(in, pool));
	}

	LocalVariableTableAttribute(String name, AssemblingInputStream in, ConstantPool pool) {
		super(name);
		List<LocalVariableEntry> table = new ArrayList<>();

		in.requireNext('{');

		while (!in.advanceIfHasNext('}')) {
			table.add(new LocalVariableEntry(in));
		}

		this.table = Collections.unmodifiableList(table);
	}


	public static LocalVariableTableAttribute emptyTable() {
		return EMPTY_TABLE;
	}

	public static LocalVariableTableAttribute emptyTypeTable() {
		return EMPTY_TYPE_TABLE;
	}


	public boolean isEmpty() {
		return table.isEmpty();
	}

	public Optional<LocalVariableEntry> findEntry(int slot, int endPos) {
		return table.stream().filter(entry -> entry.slot == slot && entry.endPos == endPos).findAny();
	}


	public static final class LocalVariableEntry {
		private final int startPos, endPos, slot;
		private final String name;
		private final Type type;

		private LocalVariableEntry(ExtendedDataInputStream in, ConstantPool pool) {
			this.startPos = in.readUnsignedShort();
			this.endPos = startPos + in.readUnsignedShort();
			this.name = pool.getUtf8String(in.readUnsignedShort());
			this.type = Type.parseType(pool.getUtf8String(in.readUnsignedShort()));
			this.slot = in.readUnsignedShort();
		}

		private LocalVariableEntry(AssemblingInputStream in) {
			this.startPos = in.nextUnsignedInt();
			this.endPos = in.nextUnsignedInt();
			this.slot = in.nextUnsignedInt();
			this.name = in.nextName();
			this.type = in.nextType();
		}

		public int startPos() {
			return startPos;
		}

		public int endPos() {
			return endPos;
		}

		public int slot() {
			return slot;
		}

		public String getName() {
			return name;
		}

		public Type getType() {
			return type;
		}
	}
}
