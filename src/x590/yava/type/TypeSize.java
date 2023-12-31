package x590.yava.type;

/**
 * Размер типа на стеке (кратен 4 байтам)
 */
public enum TypeSize {
	VOID(0), // void
	WORD(1), // boolean, byte, short, char, int, float, Object
	LONG(2); // long, double

	private final int slotsOccupied;

	TypeSize(int slotsOccupied) {
		this.slotsOccupied = slotsOccupied;
	}

	public int slotsOccupied() {
		return slotsOccupied;
	}
}
