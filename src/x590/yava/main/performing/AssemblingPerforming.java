package x590.yava.main.performing;

import x590.util.annotation.Nullable;
import x590.yava.clazz.JavaClass;
import x590.yava.exception.parsing.ParseException;
import x590.yava.io.AssemblingInputStream;
import x590.yava.io.ExtendedDataOutputStream;
import x590.yava.main.Config;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AssemblingPerforming extends AbstractPerforming<ExtendedDataOutputStream> {

	public AssemblingPerforming(Config config) {
		super(config, true);
	}

	@Override
	protected ExtendedDataOutputStream createOutputStream(OutputStream out) {
		return new ExtendedDataOutputStream(out);
	}

	@Override
	public @Nullable JavaClass read(String file) throws IOException, UncheckedIOException {

		try (var in = new AssemblingInputStream(Files.newInputStream(Paths.get(file)))) {
			return JavaClass.parse(in);

		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public void perform(JavaClass clazz) {
	}

	@Override
	public void afterPerforming(JavaClass clazz) {
	}

	@Override
	public void doWrite(JavaClass clazz) {
		clazz.serialize(out);
	}
}
