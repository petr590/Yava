package x590.yava.main.performing;

import x590.util.annotation.Nullable;
import x590.yava.clazz.JavaClass;
import x590.yava.exception.parsing.ParseException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.AssemblingOutputStream;
import x590.yava.main.Config;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class AssemblingPerforming extends AbstractPerforming<AssemblingOutputStream> {

	public AssemblingPerforming(Config config) {
		super(PerformingType.ASSEMBLE, config, true);
	}

	@Override
	protected String getOutputFileExtension() {
		return ".class";
	}

	@Override
	protected AssemblingOutputStream createOutputStream(OutputStream out) {
		return new AssemblingOutputStream(out);
	}

	@Override
	public @Nullable JavaClass read(String file) throws IOException, UncheckedIOException {

		Path path = Paths.get(file);

		try (var in = new AssemblingInputStream(Files.newInputStream(path))) {
			return JavaClass.parse(in, Objects.toString(path.getParent(), "."));

		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public void perform(JavaClass clazz) {}

	@Override
	public void afterPerforming(JavaClass clazz) {}

	@Override
	public void doWrite(JavaClass clazz) {
		clazz.serialize(out);
	}
}
