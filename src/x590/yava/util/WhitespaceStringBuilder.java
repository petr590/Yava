package x590.yava.util;

import x590.yava.clazz.ClassInfo;
import x590.yava.io.ExtendedOutputStream;

import java.util.ArrayList;
import java.util.List;

public class WhitespaceStringBuilder extends AbstractWhitespaceStringBuilder {

	private final List<String> values = new ArrayList<>();

	public WhitespaceStringBuilder() {}

	public WhitespaceStringBuilder(String str, boolean printTrailingSpace) {
		super(printTrailingSpace);
		values.add(str);
	}


	public static IWhitespaceStringBuilder empty() {
		return EmptyWhitespaceStringBuilder.NO_PRINT_TRAILING_SPACE_INSTANCE;
	}

	public static IWhitespaceStringBuilder empty(boolean printTrailingSpace) {
		return printTrailingSpace ?
				EmptyWhitespaceStringBuilder.PRINT_TRAILING_SPACE_INSTANCE :
				EmptyWhitespaceStringBuilder.NO_PRINT_TRAILING_SPACE_INSTANCE;
	}


	@Override
	public WhitespaceStringBuilder append(String str) {
		values.add(str);
		return this;
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public String toString() {
		return String.join(" ", values);
	}

	@Override
	public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {
		if (printTrailingSpace)
			out.printEach(values);
		else
			out.printAll(values);
	}


	private static class EmptyWhitespaceStringBuilder extends AbstractWhitespaceStringBuilder {

		public static final EmptyWhitespaceStringBuilder
				PRINT_TRAILING_SPACE_INSTANCE = new EmptyWhitespaceStringBuilder(true),
				NO_PRINT_TRAILING_SPACE_INSTANCE = new EmptyWhitespaceStringBuilder(false);


		private EmptyWhitespaceStringBuilder(boolean printTrailingSpace) {
			super(printTrailingSpace);
		}

		@Override
		public EmptyWhitespaceStringBuilder printTrailingSpace() {
			return PRINT_TRAILING_SPACE_INSTANCE;
		}

		@Override
		public IWhitespaceStringBuilder append(String str) {
			return new WhitespaceStringBuilder(str, printTrailingSpace);
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public String toString() {
			return "";
		}

		@Override
		public void writeTo(ExtendedOutputStream<?> out, ClassInfo classinfo) {}
	}
}
