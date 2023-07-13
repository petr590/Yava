package x590.yava.main;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import x590.argparser.ArgsNamespace;
import x590.argparser.Flag;
import x590.argparser.StandartArgParser;
import x590.argparser.option.EnumOption;
import x590.argparser.option.StringOption;
import x590.util.holder.ObjectHolder;
import x590.yava.FileSource;
import x590.yava.main.performing.AbstractPerforming.PerformingType;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public final class Config {

	public enum UsagePolicy {
		ALWAYS, AUTO, NEVER
	}

	private boolean writeToConsole = Yava.isDebug();

	private boolean showAutogenerated;
	private boolean showSynthetic;
	private boolean showBridge;

	private String indent = "    ";

	private UsagePolicy constantsUsagePolicy = UsagePolicy.ALWAYS;
	private UsagePolicy hexNumbersUsagePolicy = UsagePolicy.AUTO;

	private char longSuffix = 'L';
	private char floatSuffix = 'F';
	private char doubleSuffix = 'D';

	private boolean useLowerSuffixes;

	private boolean printDoubleSuffix;
	private boolean printTrailingZero = true;

	private boolean escapeUnicodeChars;
	private boolean printClassVersion = true;
	private boolean printImplicitModifiers = true;

	private boolean multilineStringAllowed = true;
	private boolean compactArrayInitAllowed = true;

	private boolean canOmitCurlyBrackets = true;
	private boolean canOmitThisAndClass = true;
	private boolean canOmitSingleImport;

	private boolean useOverrideAnnotation = true;
	private boolean useCStyleArray;

	private boolean printBracketsAroundBitwiseOperands = true;

	private boolean printBracketsAroundAssertStatements;
	private boolean decompileStringBuilderAsConcatenation = true;

	private boolean canSearchNestedClasses = true;

	private FileSource fileSource = FileSource.FILESYSTEM;

	private static final Locale RU = Locale.of("ru");

	static ArgsNamespace parseArguments(String[] args, ObjectHolder<PerformingType> performingTypeHolder, Builder builder) {
		return new StandartArgParser("Yava", Version.VERSION).localize()

				.add(new StringOption("files").oneOrMoreTimes()
						.help(    "Files to be processed")
						.help(RU, "Файлы для обработки"))

				.add(new Flag("-ds", "--disassemble").onParse(() -> performingTypeHolder.set(PerformingType.DISASSEMBLE))
						.help(    "Disassemble classes")
						.help(RU, "Дизассемблировать классы"))

				.add(new Flag("-as", "--assemble").onParse(() -> performingTypeHolder.set(PerformingType.ASSEMBLE))
						.help(    "Assemble classes")
						.help(RU, "Ассемблировать классы"))

				.add(new Flag("-oc", "--console").onParse(builder::writeToConsole)
						.help(    "Output to the console")
						.help(RU, "Вывести консоль"))

				.add(new Flag("-a", "--autogenerated").onParse(builder::showAutogenerated)
						.help(    "Show methods autogenerated by compiler (such as Enum.valueOf(String) or constructor by default)")
						.help(RU, "Выводить методы, автосгенерированные компилятором (например, Enum.valueOf(String) или конструктор по умолчанию)"))

				.add(new Flag("-s", "--synthetic").onParse(builder::showSynthetic)
						.help(    "Show synthetic fields, methods and classes generated by compiler")
						.help(RU, "Выводить синтетические поля, методы и классы, сгенерированные компилятором"))

				.add(new Flag("-b", "--bridge").onParse(builder::showBridge)
						.help(    "Show bridge methods generated by compiler")
						.help(RU, "Выводить bridge методы, сгенерированные компилятором"))

				.add(new Flag("-A", "--all-autogen").onParse(builder::showAllAutogenerated)
						.help(    "Show synthetic, bridge and autogenerated fields, methods and classes")
						.help(RU, "Показывать синтетические, bridge и автосгенерированные поля, методы и классы"))

				.add(new StringOption("-i", "--indent").onParse(builder::setIndent)
						.help(    "Set indent (by default four spaces)")
						.help(RU, "Установить отступ (по умолчанию четыре пробела)"))

				.add(new Flag("-t", "--tab").onParse(() -> builder.setIndent("\t"))
						.help(    "Use tab as indent")
						.help(RU, "Использовать табуляцию в качестве отступа"))

				.add(new EnumOption<>(UsagePolicy.class, "-c", "--constants").onParse(builder::constantsUsagePolicy)
						.implicitValue(UsagePolicy.ALWAYS)
						.help(    "Use constants: always - always use, auto - use only general constants (Integer.MAX_VALUE, Math.PI, etc.), never - never use")
						.help(RU, "Использовать константы: always - всегда использовать, auto - использовать только общие константы (Integer.MAX_VALUE, Math.PI и т.д.), never - никогда не использовать"))

				.add(new EnumOption<>(UsagePolicy.class, "-x", "--hex").onParse(builder::hexNumbersUsagePolicy)
						.implicitValue(UsagePolicy.ALWAYS)
						.help(    "Use hex numbers: always - always use, auto - only for values like 0x7F, 0x80 and 0xFF, never - never use")
						.help(RU, "Использовать шестнадцатеричные числа: always - всегда использовать, auto - только для значений типа 0x7F, 0x80 и 0xFF, never - никогда не использовать"))

				.add(new Flag("-l", "--low-literals").onParse(builder::useLowerSuffixes)
						.help(    "Print lower letter literals for long, float and double")
						.help(RU, "Выводить литералы со строчными буквами для long, float и double"))

				.add(new Flag("-D", "--double-suffix").onParse(builder::printDoubleSuffix)
						.help(    "Print suffix for double literals")
						.help(RU, "Выводить суффикс для литералов double"))

				.add(new Flag("-Z", "--no-trailing-zero").onParse(not(builder::printTrailingZero))
						.help(    "No print trailing zero for float and double literals")
						.help(RU, "Не выводить конечный ноль для литералов float и double"))

				.add(new Flag("--esc-utf").onParse(builder::escapeUnicodeChars)
						.help(    "Escape multibyte unicode characters")
						.help(RU, "Экранировать многобайтовые символы юникода"))

				.add(new Flag("-V", "--print-version").onParse(builder::printClassVersion)
						.help(    "Print version for each class")
						.help(RU, "Выводить версию для каждого класса"))

				.add(new Flag("-I", "--no-print-implicit-modifiers").onParse(not(builder::printImplicitModifiers))
						.help(    "Don't print implied modifiers (`public` for interfaces or `private` for enum constructor)")
						.help(RU, "Не выводить неявные модификаторы (`public` для интерфейсов или `private` для конструктора enum)"))

				.add(new Flag("-M", "--no-multiline-string").onParse(not(builder::multilineStringAllowed))
						.help(    "Don't split multiline strings")
						.help(RU, "Не разделять многострочные строки"))

				.add(new Flag("--no-compact-arr-init").onParse(not(builder::compactArrayInitAllowed))
						.help(    "Don't use compact array initialization (like int[] arr = {})")
						.help(RU, "Не использовать компактную инициализацию массива (например, int[] arr = {})"))

				.add(new Flag("-B", "--no-omit-curly-brackets").onParse(not(builder::canOmitCurlyBrackets))
						.help(    "Don't omit curly brackets if the scope contains one expression or does not contain any")
						.help(RU, "Не опускать фигурные скобки, если область содержит одно выражение или не содержит ни одного"))

				.add(new Flag("-T", "--no-omit-this-and-class").onParse(not(builder::canOmitThisAndClass))
						.help(    "Don't omit `this` keyword and this class in fields and methods access")
						.help(RU, "Не опускать ключевое слово `this` и текущий класс при доступе к полям и методам"))

				.add(new Flag("--omit-single-import").onParse(builder::canOmitSingleImport)
						.help(    "Omit import if class is uses only one time")
						.help(RU, "Опустить импорт, если класс используется только один раз"))

				.add(new Flag("--no-use-override").onParse(not(builder::useOverrideAnnotation))
						.help(    "Don't use the \"java.lang.Override\" annotation")
						.help(RU, "Не использовать аннотацию \"java.lang.Override\""))

				.add(new Flag("--c-style-array").onParse(builder::useCStyleArray)
						.help(    "Use C-style array declaration")
						.help(RU, "Использовать объявление массива в стиле C"))

				.add(new Flag("--no-brackets-around-bitwise-operands").onParse(not(builder::printBracketsAroundBitwiseOperands))
						.help(    "Don't print brackets around bitwise operands")
						.help(RU, "Не выводить скобки вокруг побитовых операндов"))

				.add(new Flag("--brackets-around-asserts").onParse(builder::printBracketsAroundAssertStatements)
						.help(    "Print brackets around `assert` statements")
						.help(RU, "Выводить скобки вокруг операторов `assert`"))

				.add(new Flag("--no-decompile-string-builder-as-concatenation").onParse(not(builder::decompileStringBuilderAsConcatenation))
						.help(    "Don't decompile StringBuilder as concatenation")
						.help(RU, "Не декомпилировать StringBuilder как конкатенацию"))

				.add(new Flag("--no-search-nested-classes").onParse(not(builder::canSearchNestedClasses))
						.help(    "Don't search for nested classes in the directory from which the outer class is loaded")
						.help(RU, "Не искать вложенные классы в папке, из которой загружается внешний класс"))

				.add(new Flag("-jdk", "--jdk").onParse(() -> builder.fileSource(FileSource.JDK))
						.help(    "Search classes in JDK")
						.help(RU, "Искать классы в JDK"))

				.parse(args);
	}

	private static BooleanConsumer not(BooleanConsumer consumer) {
		return value -> consumer.accept(!value);
	}

	public boolean writeToConsole() {
		return writeToConsole;
	}

	public String getIndent() {
		return indent;
	}

	public boolean showAutogenerated() {
		return showAutogenerated;
	}

	public boolean showSynthetic() {
		return showSynthetic;
	}

	public boolean showBridge() {
		return showBridge;
	}

	public UsagePolicy constantsUsagePolicy() {
		return constantsUsagePolicy;
	}

	public UsagePolicy hexNumbersUsagePolicy() {
		return hexNumbersUsagePolicy;
	}

	public char getLongSuffix() {
		return longSuffix;
	}

	public char getFloatSuffix() {
		return floatSuffix;
	}

	public char getDoubleSuffix() {
		return doubleSuffix;
	}

	public boolean useLowerSuffixes() {
		return useLowerSuffixes;
	}

	public boolean printDoubleSuffix() {
		return printDoubleSuffix;
	}

	public boolean printTrailingZero() {
		return printTrailingZero;
	}

	public boolean escapeUnicodeChars() {
		return escapeUnicodeChars;
	}

	public boolean printClassVersion() {
		return printClassVersion;
	}

	public boolean printImplicitModifiers() {
		return printImplicitModifiers;
	}

	public boolean multilineStringAllowed() {
		return multilineStringAllowed;
	}

	public boolean compactArrayInitAllowed() {
		return compactArrayInitAllowed;
	}

	public boolean canOmitCurlyBrackets() {
		return canOmitCurlyBrackets;
	}

	public boolean canOmitThisAndClass() {
		return canOmitThisAndClass;
	}

	public boolean canOmitSingleImport() {
		return canOmitSingleImport;
	}

	public boolean useOverrideAnnotation() {
		return useOverrideAnnotation;
	}

	public boolean useCStyleArray() {
		return useCStyleArray;
	}

	public boolean printBracketsAroundBitwiseOperands() {
		return printBracketsAroundBitwiseOperands;
	}

	public boolean printBracketsAroundAssertStatements() {
		return printBracketsAroundAssertStatements;
	}

	public boolean decompileStringBuilderAsConcatenation() {
		return decompileStringBuilderAsConcatenation;
	}

	public boolean canSearchNestedClasses() {
		return canSearchNestedClasses;
	}

	public FileSource getFileSource() {
		return fileSource;
	}

	private Config() {}

	/**
	 * Возвращает новый конфиг со значениями по умолчанию
	 */
	public static Config newDefaultConfig() {
		return new Config();
	}

	/**
	 * Создаёт новый {@link Builder} и возвращает его
	 */
	public static Builder newBuilder() {
		return new Builder(new Config());
	}

	public static class Builder {

		private Config config;

		private Builder(Config config) {
			this.config = config;
		}

		/**
		 * Возвращает построенный объект, после чего использоапние билдера становится невозможным
		 */
		public Config build() {
			Config config = this.config;
			this.config = null;

			return config;
		}


		public Builder withArguments(String... args) {
			args = Arrays.copyOf(args, args.length + 1);
			args[args.length - 1] = "";
			parseArguments(args, ObjectHolder.voidHolder(), this);
			return this;
		}


		public Builder writeToConsole(boolean writeToConsole) {
			config.writeToConsole = writeToConsole;
			return this;
		}

		public Builder setIndent(String indent) {
			config.indent = Objects.requireNonNull(indent);
			return this;
		}

		public Builder showAutogenerated(boolean showAutogenerated) {
			config.showAutogenerated = showAutogenerated;
			return this;
		}

		public Builder showSynthetic(boolean showSynthetic) {
			config.showSynthetic = showSynthetic;
			return this;
		}

		public Builder showBridge(boolean showBridge) {
			config.showBridge = showBridge;
			return this;
		}

		public Builder showAllAutogenerated(boolean show) {
			var config = this.config;
			config.showAutogenerated = config.showSynthetic = config.showBridge = show;

			return this;
		}

		public Builder constantsUsagePolicy(UsagePolicy constantsUsagePolicy) {
			config.constantsUsagePolicy = Objects.requireNonNull(constantsUsagePolicy);
			return this;
		}

		public Builder hexNumbersUsagePolicy(UsagePolicy hexNumbersUsagePolicy) {
			config.hexNumbersUsagePolicy = Objects.requireNonNull(hexNumbersUsagePolicy);
			return this;
		}

		public Builder useLowerSuffixes(boolean useLowerSuffixes) {
			var config = this.config;
			config.useLowerSuffixes = useLowerSuffixes;

			if (useLowerSuffixes) {
				config.longSuffix = 'l';
				config.floatSuffix = 'f';
				config.doubleSuffix = 'd';
			} else {
				config.longSuffix = 'L';
				config.floatSuffix = 'F';
				config.doubleSuffix = 'D';
			}

			return this;
		}

		public Builder printDoubleSuffix(boolean printDoubleSuffix) {
			config.printDoubleSuffix = printDoubleSuffix;
			return this;
		}

		public Builder printTrailingZero(boolean printTrailingZero) {
			config.printTrailingZero = printTrailingZero;
			return this;
		}

		public Builder escapeUnicodeChars(boolean escapeUnicodeChars) {
			config.escapeUnicodeChars = escapeUnicodeChars;
			return this;
		}

		public Builder printClassVersion(boolean printClassVersion) {
			config.printClassVersion = printClassVersion;
			return this;
		}

		public Builder printImplicitModifiers(boolean printImplicitModifiers) {
			config.printImplicitModifiers = printImplicitModifiers;
			return this;
		}

		public Builder multilineStringAllowed(boolean multilineStringAllowed) {
			config.multilineStringAllowed = multilineStringAllowed;
			return this;
		}

		public Builder compactArrayInitAllowed(boolean compactArrayInitAllowed) {
			config.compactArrayInitAllowed = compactArrayInitAllowed;
			return this;
		}

		public Builder canOmitCurlyBrackets(boolean canOmitCurlyBrackets) {
			config.canOmitCurlyBrackets = canOmitCurlyBrackets;
			return this;
		}

		public Builder canOmitThisAndClass(boolean canOmitThisAndClass) {
			config.canOmitThisAndClass = canOmitThisAndClass;
			return this;
		}

		public Builder canOmitSingleImport(boolean canOmitSingleImport) {
			config.canOmitSingleImport = canOmitSingleImport;
			return this;
		}

		public Builder useOverrideAnnotation(boolean useOverrideAnnotation) {
			config.useOverrideAnnotation = useOverrideAnnotation;
			return this;
		}

		public Builder useCStyleArray(boolean useCStyleArray) {
			config.useCStyleArray = useCStyleArray;
			return this;
		}

		public Builder printBracketsAroundBitwiseOperands(boolean printBracketsAroundBitwiseOperands) {
			config.printBracketsAroundBitwiseOperands = printBracketsAroundBitwiseOperands;
			return this;
		}

		public Builder printBracketsAroundAssertStatements(boolean printBracketsAroundAssertStatements) {
			config.printBracketsAroundAssertStatements = printBracketsAroundAssertStatements;
			return this;
		}

		public Builder decompileStringBuilderAsConcatenation(boolean decompileStringBuilderAsConcatenation) {
			config.decompileStringBuilderAsConcatenation = decompileStringBuilderAsConcatenation;
			return this;
		}

		public Builder canSearchNestedClasses(boolean canSearchNestedClasses) {
			config.canSearchNestedClasses = canSearchNestedClasses;
			return this;
		}

		public Builder fileSource(FileSource fileSource) {
			config.fileSource = fileSource;
			return this;
		}
	}
}
