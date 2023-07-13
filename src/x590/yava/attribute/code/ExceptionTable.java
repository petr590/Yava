package x590.yava.attribute.code;

import x590.util.Logger;
import x590.util.LoopUtil;
import x590.util.Util;
import x590.util.annotation.Immutable;
import x590.util.annotation.Nullable;
import x590.yava.attribute.Sizes;
import x590.yava.constpool.ConstantPool;
import x590.yava.context.Context;
import x590.yava.context.DecompilationContext;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.yava.scope.CatchScope;
import x590.yava.scope.FinallyScope;
import x590.yava.scope.Scope;
import x590.yava.scope.TryScope;
import x590.yava.serializable.JavaSerializableWithPool;
import x590.yava.type.reference.ClassType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExceptionTable implements JavaSerializableWithPool {

	static final ExceptionTable EMPTY_TABLE = new ExceptionTable();

	private final @Immutable List<TryEntry> entries;

	private ExceptionTable() {
		this.entries = Collections.emptyList();
	}

	public ExceptionTable(ExtendedDataInputStream in, ConstantPool pool) {
		int size = in.readUnsignedShort();

		List<TryEntry> entries = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			TryEntry.readTo(in, pool, entries);
		}

		Collections.sort(entries);
		entries.forEach(TryEntry::freeze);

		this.entries = Collections.unmodifiableList(entries);
	}

	public ExceptionTable(AssemblingInputStream in, CodeAttribute.LabelsManager labelsManager) {
		List<TryEntry> entries = new ArrayList<>();

		in.requireNext('{');

		labelsManager.resetPos();

		while (!in.advanceIfHasNext('}')) {
			TryEntry.parseTo(in, labelsManager, entries);
		}

		Logger.debug("finally", entries);

//		Collections.sort(entries);
		entries.forEach(TryEntry::freeze);

		this.entries = Collections.unmodifiableList(entries);
	}

	public static ExceptionTable empty() {
		return EMPTY_TABLE;
	}

	public @Immutable List<TryEntry> getEntries() {
		return entries;
	}

	public int rawCatchEntriesCount() {
		return entries.stream().mapToInt(TryEntry::rawCatchEntriesCount).sum();
	}

	@Override
	public void serialize(AssemblingOutputStream out, ConstantPool pool) {
		out .recordShortSize(rawCatchEntriesCount())
			.recordAllNoSized(entries, pool);
	}

	public int getFullLength() {
		return Sizes.LENGTH + CatchEntry.SIZE * rawCatchEntriesCount();
	}


	public static class TryEntry implements Comparable<TryEntry>, JavaSerializableWithPool {
		private final int startPos, endPos;
		private @Immutable List<CatchEntry> catchEntries = new ArrayList<>();

		private TryEntry(int startPos, int endPos) {
			this.startPos = startPos;
			this.endPos = endPos;
		}

		public int rawCatchEntriesCount() {
			return catchEntries.stream().mapToInt(CatchEntry::rawCatchEntriesCount).sum();
		}

		public void addCatchEntry(@Nullable CatchEntry catchEntry) {
			if (catchEntry != null)
				catchEntries.add(catchEntry);
		}

		public int getStartPos() {
			return startPos;
		}

		public int getEndPos() {
			return endPos;
		}

		public int getStartIndex(Context context) {
			return context.posToIndex(startPos);
		}

		public int getEndIndex(Context context) {
			return context.posToIndex(endPos);
		}

		public int getFactualEndIndex(Context context) {
			return getEndIndex(context) - (isFinally() ? 1 : 0);
		}

		public @Immutable List<CatchEntry> getCatchEntries() {
			return catchEntries;
		}

		public boolean isFinally() {
			return catchEntries.stream().allMatch(CatchEntry::isFinally);
		}

		public void setLastPos(int lastCatchEntryEndPos) {
			catchEntries.get(catchEntries.size() - 1).setEndPos(lastCatchEntryEndPos);
		}

		private void freeze() {
			Collections.sort(catchEntries);

			LoopUtil.forEachPair(catchEntries, (entry1, entry2) -> {
				entry1.setEndPos(entry2.getStartPos());
				entry1.setHasNext();
			});

			catchEntries.forEach(CatchEntry::freeze);
			this.catchEntries = Collections.unmodifiableList(catchEntries);
		}

		private static void readTo(ExtendedDataInputStream in, ConstantPool pool, List<TryEntry> entries) {
			TryEntry tryEntry = findOrCreate(entries, in.readUnsignedShort(), in.readUnsignedShort());
			CatchEntry.readTo(in, pool, tryEntry.catchEntries);
		}

		public static void parseTo(AssemblingInputStream in, CodeAttribute.LabelsManager labelsManager, List<TryEntry> entries) {
			TryEntry tryEntry = findOrCreate(entries, in.nextLabel(labelsManager), in.nextLabel(labelsManager));
			CatchEntry.parseTo(in, labelsManager, tryEntry.catchEntries);
		}

		private static TryEntry findOrCreate(List<TryEntry> entries, int startPos, int endPos) {
			Logger.debug(startPos, endPos);

			var r = entries.stream()
					.filter(entry -> entry.startPos == startPos && entry.endPos == endPos).findAny()
					.orElseGet(() -> Util.addAndGetBack(entries, new TryEntry(startPos, endPos)));

			Logger.debug(r, entries);

			return r;
		}

		public Scope createScope(DecompilationContext context) {
			return new TryScope(context, getFactualEndIndex(context) + 1);
		}

		@Override
		public int compareTo(TryEntry other) {
			int diff = other.startPos - startPos;
			if (diff != 0)
				return diff;

			return other.endPos - endPos;
		}

		@Override
		public void serialize(AssemblingOutputStream out, ConstantPool pool) {
			var startPos = this.startPos;
			var endPos = this.endPos;

			catchEntries.forEach(entry ->
					entry.exceptionTypes.forEach(exceptionType ->
							out .recordShort(startPos)
								.recordShort(endPos)
								.recordShort(entry.startPos)
								.recordShort(pool.classIndexForNullable(exceptionType))
					)
			);
		}

		@Override
		public String toString() {
			return String.format("TryEntry {%d - %d, %s}",
					startPos, endPos, catchEntries);
		}
	}


	public static class CatchEntry implements Comparable<CatchEntry> {

		public static final int SIZE = Sizes.SHORT * 3 + Sizes.CONSTPOOL_INDEX;

		private static final int NPOS = -1;

		private final int startPos;
		private @Immutable List<@Nullable ClassType> exceptionTypes = new ArrayList<>();
		private int endPos = NPOS;
		private boolean isFinally, hasNext;

		public int rawCatchEntriesCount() {
			return exceptionTypes.size();
		}

		private void addExceptionType(ConstantPool pool, int exceptionTypeIndex) {
			addExceptionType(exceptionTypeIndex != 0 ?
					pool.getClassConstant(exceptionTypeIndex).toClassType() :
					null);
		}

		private void addExceptionType(ClassType exceptionType) {
			if (exceptionType == null) {
				isFinally = true;
			}

			exceptionTypes.add(exceptionType);
		}

		private CatchEntry(int startPos) {
			this.startPos = startPos;
		}

		public int getStartPos() {
			return startPos;
		}

		public int getEndPos() {
			return endPos;
		}

		public int getEndIndex(DecompilationContext context) {
			var endPos = this.endPos;
			return endPos != NPOS ? context.posToIndex(endPos) : context.currentScope().endIndex();
		}

		public @Immutable List<@Nullable ClassType> getExceptionTypes() {
			return exceptionTypes;
		}

		public boolean isFinally() {
			return isFinally;
		}

		public boolean hasNext() {
			return hasNext;
		}

		private void setHasNext() {
			hasNext = true;
		}

		private void setEndPos(int endPos) {
			this.endPos = endPos;
		}

		private void freeze() {
			this.exceptionTypes = Collections.unmodifiableList(exceptionTypes);
		}

		private static void readTo(ExtendedDataInputStream in, ConstantPool pool, List<CatchEntry> entries) {

			int startPos = in.readUnsignedShort(),
				exceptionTypeIndex = in.readUnsignedShort();

			findOrCreate(entries, startPos, exceptionTypeIndex == 0)
					.addExceptionType(pool, exceptionTypeIndex);
		}

		public static void parseTo(AssemblingInputStream in, CodeAttribute.LabelsManager labelsManager, List<CatchEntry> entries) {
			int startPos = in.nextLabel(labelsManager);
			ClassType exceptionType = in.nextNullableClassType();

			findOrCreate(entries, startPos, exceptionType == null)
					.addExceptionType(exceptionType);
		}

		private static CatchEntry findOrCreate(List<CatchEntry> entries, int startPos, boolean isFinally) {
			return entries.stream()
					.filter(entry -> entry.startPos == startPos && entry.isFinally() == isFinally).findAny()
					.orElseGet(() -> Util.addAndGetBack(entries, new CatchEntry(startPos)));
		}

		public Scope createScope(DecompilationContext context) {
			return isFinally() ?
					new FinallyScope(context, getEndIndex(context), hasNext) :
					new CatchScope(context, getEndIndex(context), exceptionTypes, hasNext);
		}

		@Override
		public int compareTo(CatchEntry other) {
			return startPos - other.startPos;
		}

		@Override
		public String toString() {
			return String.format("CatchEntry {%d - %d, %s}",
					startPos, endPos,
					exceptionTypes.stream()
							.map(classType -> classType == null ? "null" : classType.getName())
							.collect(Collectors.joining(" | ")));
		}
	}
}
