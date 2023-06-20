package x590.yava.main;

import x590.argparser.ArgsNamespace;
import x590.util.annotation.Immutable;
import x590.util.holder.ObjectHolder;
import x590.yava.main.Config.Builder;
import x590.yava.main.performing.AbstractPerforming.PerformingType;
import x590.yava.main.performing.Performing;

import java.util.Collections;
import java.util.List;

public final class Yava {

	private static boolean isDebug;

	public static void setDebug(boolean isDebug) {
		Yava.isDebug = isDebug;
	}

	public static boolean isDebug() {
		return isDebug;
	}

	private static Yava instance;

	public static Yava getInstance() {
		if (instance != null)
			return instance;

		throw new IllegalStateException("Yava yet not initialized");
	}

	public static Config getConfig() {
		return getInstance().config;
	}


	private final @Immutable List<String> files;

	private final PerformingType performingType;

	private final Config config;


	public static void init(String... args) {
		if (instance != null)
			throw new IllegalStateException("Yava already initialized");

		instance = new Yava(args);
	}

	public static void init(@Immutable List<String> files, PerformingType performingType, Config config) {
		if (instance != null)
			throw new IllegalStateException("Yava already initialized");

		instance = new Yava(files, performingType, config);
	}

	public static void init(PerformingType performingType, Config config) {
		init(Collections.emptyList(), performingType, config);
	}


	private Yava(String... args) {

		ObjectHolder<PerformingType> performingTypeHolder = new ObjectHolder<>(PerformingType.DECOMPILE);

		Builder builder = Config.newBuilder();

		ArgsNamespace arguments = Config.parseArguments(args, performingTypeHolder, builder);

		this.files = Collections.unmodifiableList(arguments.getAll("files"));
		this.performingType = performingTypeHolder.get();
		this.config = builder.build();
	}

	private Yava(@Immutable List<String> files, PerformingType performingType, Config config) {
		this.files = files;
		this.performingType = performingType;
		this.config = config;
	}


	public @Immutable List<String> getFiles() {
		return files;
	}


	public PerformingType getPerformingType() {
		return performingType;
	}


	public Performing<?> getPerforming() {
		return performingType.getPerforming(config);
	}
}
