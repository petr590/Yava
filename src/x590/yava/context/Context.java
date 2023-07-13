package x590.yava.context;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import x590.yava.constpool.ConstantPool;
import x590.yava.exception.decompilation.NoSuchIndexException;

public abstract class Context {

	public static final int NONE_INDEX = -1;

	public final ConstantPool pool;
	int index;
	final Int2IntMap indexMap, posMap;


	public Context(Context otherContext) {
		this.pool = otherContext.pool;
		this.indexMap = otherContext.indexMap;
		this.posMap = otherContext.posMap;
		this.index = 0;
	}

	public Context(ConstantPool pool, int mapLength) {
		this(pool, new Int2IntArrayMap(mapLength), new Int2IntArrayMap(mapLength));
	}

	public Context(ConstantPool pool, Int2IntMap indexMap, Int2IntMap posMap) {
		this.pool = pool;
		this.indexMap = indexMap;
		this.posMap = posMap;
	}


	public int currentIndex() {
		return index;
	}

	// pos смещается, когда мы читаем аргументы инструкции,
	// поэтому возвращаем позицию по индексу из таблицы
	public int currentPos() {
		return getOrThrow(posMap, index);
	}


	public int posToIndex(int pos) {
		return getOrThrow(indexMap, pos);
	}

	public int indexToPos(int index) {
		return getOrThrow(posMap, index);
	}


	private static int getOrThrow(Int2IntMap table, int index) {
		int value = table.getOrDefault(index, NONE_INDEX);
		if (value != NONE_INDEX) {
			return value;
		}

		throw new NoSuchIndexException(index, table);
	}


	public abstract void warning(String message);
}
