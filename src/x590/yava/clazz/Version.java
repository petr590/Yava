package x590.yava.clazz;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import x590.yava.exception.parsing.ParseException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataInputStream;
import x590.util.annotation.Immutable;

/**
 * Версия class-файла. Содержит номера версий major и minor.
 * Автоматически определяет версию Java по номеру major.
 */
@Immutable
public record Version(int major, int minor) {

	// I have not found an official indication of version JDK Beta, JDK 1.0 number,
	// and I'm too lazy to check it
	public static final int
			JDK_BETA = 43, JDK_1_0 = 44, JDK_1_1 = 45, JAVA_1_2 = 46, JAVA_1_3 = 47, JAVA_1_4 = 48,
			JAVA_5   = 49, JAVA_6  = 50, JAVA_7  = 51, JAVA_8   = 52, JAVA_9   = 53, JAVA_10  = 54,
			JAVA_11  = 55, JAVA_12 = 56, JAVA_13 = 57, JAVA_14  = 58, JAVA_15  = 59, JAVA_16  = 60,
			JAVA_17  = 61, JAVA_18 = 62, JAVA_19 = 63, JAVA_20 = 64,
			FIRST_VERSION = JDK_BETA, LAST_VERSION = JAVA_20;
	
	private static final Int2ObjectMap<String> VERSIONS = new Int2ObjectOpenHashMap<>(LAST_VERSION - FIRST_VERSION + 1, 1);
	
	static {
		VERSIONS.put(JDK_BETA, "JDK Beta");
		VERSIONS.put(JDK_1_0,  "JDK 1.0");
		VERSIONS.put(JDK_1_1,  "JDK 1.1");
		VERSIONS.put(JAVA_1_2, "Java 1.2");
		VERSIONS.put(JAVA_1_3, "Java 1.3");
		VERSIONS.put(JAVA_1_4, "Java 1.4");
		VERSIONS.put(JAVA_5,   "Java 5");
		VERSIONS.put(JAVA_6,   "Java 6");
		VERSIONS.put(JAVA_7,   "Java 7");
		VERSIONS.put(JAVA_8,   "Java 8");
		VERSIONS.put(JAVA_9,   "Java 9");
		VERSIONS.put(JAVA_10,  "Java 10");
		VERSIONS.put(JAVA_11,  "Java 11");
		VERSIONS.put(JAVA_12,  "Java 12");
		VERSIONS.put(JAVA_13,  "Java 13");
		VERSIONS.put(JAVA_14,  "Java 14");
		VERSIONS.put(JAVA_15,  "Java 15");
		VERSIONS.put(JAVA_16,  "Java 16");
		VERSIONS.put(JAVA_17,  "Java 17");
		VERSIONS.put(JAVA_18,  "Java 18");
		VERSIONS.put(JAVA_19,  "Java 19");
		VERSIONS.put(JAVA_20,  "Java 20");
	}



	private static final Int2ObjectMap<Version> INSTANCES = new Int2ObjectArrayMap<>();

	/** Используйте {@link #of(short, short)} вместо этого конструктора */
	@Deprecated
	public Version {}

	private Version(int versions) {
		this(versions & 0xFFFF, versions >>> 16);
	}


	public static Version of(short major, short minor) {
		return INSTANCES.computeIfAbsent(minor << 16 | major & 0xFFFF, Version::new);
	}

	public static Version read(ExtendedDataInputStream in) {
		int version = in.readInt();
		return INSTANCES.computeIfAbsent(version, Version::new);
	}

	private static short safeCastToShort(int value) {
		if((value & 0xFFFF) != value) {
			throw new ParseException("Version number " + value + " is too big");
		}

		return (short)value;
	}

	public static Version parse(AssemblingInputStream in) {
		in.requireNext("version").requireNext('=');
		short major = safeCastToShort(in.nextUnsignedInt());
		in.requireNext('.');
		short minor = safeCastToShort(in.nextUnsignedInt());
		in.requireNext(';');
		return of(major, minor);
	}

	@Override
	public String toString() {
		String versionName = major > LAST_VERSION ?
				VERSIONS.get(LAST_VERSION) + '+' :
				VERSIONS.get(major);

		return major + "." + minor + (versionName != null ? " (" + versionName + ")" : "");
	}
}
