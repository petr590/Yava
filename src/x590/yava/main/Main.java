package x590.yava.main;

import x590.util.function.throwing.ThrowingBiConsumer;
import x590.yava.clazz.JavaClass;
import x590.yava.main.performing.Performing;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Точка входа программы
 */
public final class Main {

	private Main() {
	}

	public static void main(String[] args) {

		Yava.init(args);

		final Yava yava = Yava.getInstance();

		final List<JavaClass> classes = new ArrayList<>(yava.getFiles().size());

		final Performing<?> performing = yava.getPerforming();


		for (String file : yava.getFiles()) {
			JavaClass clazz = performing.readSafe(file);
			if (clazz != null)
				classes.add(clazz);
		}

		try {
			performing.setup();
		} catch (IOException | UncheckedIOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}


		classes.stream()
				.filter(clazz -> !clazz.canStringify() && clazz.getModifiers().isSynthetic())
				.forEach(clazz -> System.out.println("Ignored " + clazz));

		List<JavaClass> stringifiableClasses = classes.stream().filter(JavaClass::canStringify)
				.collect(Collectors.toList());

		forEach(stringifiableClasses, performing, Performing::perform);
		forEach(stringifiableClasses, performing, Performing::afterPerforming);
		forEach(stringifiableClasses, performing, Performing::write);

		performing.finalizePerforming();

		try {
			performing.close();
		} catch (IOException | UncheckedIOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private static <E extends Exception> void forEach(List<JavaClass> classes, Performing<?> performing,
													  ThrowingBiConsumer<Performing<?>, JavaClass, E> action) {

		for (Iterator<JavaClass> iter = classes.iterator(); iter.hasNext(); ) {
			try {
				action.accept(performing, iter.next());

			} catch (Exception ex) {
				iter.remove();

				// Если исключение возникло при выводе файла в консоль,
				// надо, чтобы стектрейс начинался с новой строки.
				System.err.println();
				ex.printStackTrace();
			}
		}
	}
}
