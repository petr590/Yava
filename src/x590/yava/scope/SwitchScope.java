package x590.yava.scope;

import it.unimi.dsi.fastutil.ints.*;
import x590.util.annotation.Nullable;
import x590.yava.clazz.Version;
import x590.yava.constpool.ConstantPool;
import x590.yava.constpool.IntegerConstant;
import x590.yava.context.DecompilationContext;
import x590.yava.context.StringifyContext;
import x590.yava.io.StringifyOutputStream;
import x590.yava.operation.Operation;
import x590.yava.operation.OperationUtils;
import x590.yava.type.primitive.PrimitiveType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwitchScope extends Scope {

	private Operation value;

	private final int defaultIndex;

	/**
	 * key: index, value: case values
	 */
	private final Int2ObjectMap<List<IntegerConstant>> indexTable;

	/**
	 * Содержит индексы в обратном порядке
	 */
	private final IntList indexes;

	private List<CaseScope> cases;

	private boolean expanded;

	private @Nullable Int2ObjectMap<String> enumTable;

	/**
	 * offsetTable: key: case value, value: offset
	 */
	public SwitchScope(DecompilationContext context, int defaultIndex, Int2IntMap offsetTable) {
		super(context, defaultIndex);

		this.value = context.popAsNarrowest(PrimitiveType.INT);

		this.defaultIndex = defaultIndex;

		this.indexTable = new Int2ObjectOpenHashMap<>(offsetTable.size());


		for (var entry : offsetTable.int2IntEntrySet()) {
			indexTable.computeIfAbsent(
					context.posToIndex(entry.getIntValue() + context.currentPos()),
					key -> new ArrayList<>()
			).add(ConstantPool.findOrCreateConstant(entry.getIntKey()));
		}


		this.indexes = new IntArrayList(indexTable.keySet());

		if (!indexTable.containsKey(defaultIndex))
			indexes.add(defaultIndex);

		indexes.sort(IntComparators.OPPOSITE_COMPARATOR); // Сортировка от большего к меньшему

		setEndIndex(indexes.isEmpty() ? defaultIndex : defaultIndex > indexes.getInt(0) ? defaultIndex : superScope().endIndex());
	}


	/**
	 * Расширяет {@code switch} до указанного индекса.
	 *
	 * @return {@code true}, если расширение удалось или {@code switch} уже расширен до этого индекса,
	 * {@code false} в противном случае.
	 */
	@Override
	public boolean expandTo(int newEndIndex) {
		if (!expanded) {
			if (newEndIndex <= superScope().endIndex()) {
				setEndIndex(newEndIndex);
				expanded = true;
				return true;
			}

			return false;
		}

		return endIndex() == newEndIndex;
	}

	@Override
	public void finalizeScope(DecompilationContext context) {

		List<CaseScope> cases = this.cases = new ArrayList<>(indexes.size());

		int prevIndex = endIndex();
		boolean defaultCaseUsed = false;

		boolean isLastCase = true;

		for (var iter = indexes.iterator(); iter.hasNext(); ) {

			int index = iter.nextInt();

			boolean useDefaultCase = false;

			if (!defaultCaseUsed && index == defaultIndex) {
				useDefaultCase = defaultCaseUsed = true;
			}

			List<IntegerConstant> constants = indexTable.getOrDefault(index, Collections.emptyList());
			List<Operation> caseOperations = pullOperationsFromIndex(index);

			// Проверка на пустой defult case, стоящий в конце switch
			if (!isLastCase || !useDefaultCase || !constants.isEmpty() || !caseOperations.isEmpty()) {
				cases.add(0, new CaseScope(index, prevIndex, this, constants,
						useDefaultCase, isLastCase, caseOperations));

				isLastCase = false;
			}

			prevIndex = index;
		}

//		assert this.isEmpty();

		for (CaseScope caseScope : cases)
			addOperation(caseScope, caseScope.startIndex());


		if (context.getClassinfo().getVersion().major() >= Version.JAVA_12 &&
				cases.stream().allMatch(CaseScope::canUseNewSwitch)) {

			cases.forEach(CaseScope::useNewSwitch);
			cases.forEach(Scope::deleteRemovedOperations);
		}

		super.finalizeScope(context);
	}


	@Override
	public boolean isBreakable() {
		return true;
	}

	@Override
	public boolean isTerminable() {
		return cases.stream().anyMatch(CaseScope::usesDefaultCase) &&
				cases.stream().allMatch(CaseScope::isTerminable);
	}


	@Override
	protected void writeHeader(StringifyOutputStream out, StringifyContext context) {
		out.print("switch(").print(value, context).print(')');
	}


	@Override
	public @Nullable Int2ObjectMap<String> getEnumTable(DecompilationContext context) {
		return enumTable;
	}


	@Override
	public void setEnumTable(@Nullable Int2ObjectMap<String> enumTable) {
		this.enumTable = enumTable;
		cases.forEach(caseScope -> caseScope.setEnumTable(enumTable));
	}


	@Override
	public void afterDecompilation(DecompilationContext context) {
		Operation enumValue = OperationUtils.getEnumValueInSwitch(context, value, this::setEnumTable);

		if (enumValue != null) {
			this.value = enumValue;
		}
	}


	@Override
	protected boolean canOmitCurlyBrackets() {
		return false;
	}
}
