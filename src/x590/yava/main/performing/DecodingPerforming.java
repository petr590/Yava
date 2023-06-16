package x590.yava.main.performing;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import x590.yava.clazz.JavaClass;
import x590.yava.exception.disassembling.DisassemblingException;
import x590.yava.io.ExtendedOutputStream;
import x590.yava.main.Config;
import x590.util.Logger;
import x590.util.Timer;
import x590.util.annotation.Nullable;

import static java.io.File.separatorChar;

public abstract class DecodingPerforming<S extends ExtendedOutputStream<S>> extends AbstractPerforming<S> {
	
	public DecodingPerforming(Config config) {
		super(config);
	}

	private static final Pattern CLASSNAME_PATTERN =
		Pattern.compile("([\\w$-]+)(\\.[\\w$-]+)+", Pattern.UNICODE_CASE);

	private static Path toPath(String filename) {
		return Paths.get(
				CLASSNAME_PATTERN.matcher(filename).matches() ?
						filename.replace('.', separatorChar) + ".class" :
						filename
		);
	}
	
	@Override
	public @Nullable JavaClass read(String filename) throws IOException, UncheckedIOException {

		Path path = toPath(filename);

		DataInputStream in = new DataInputStream(new BufferedInputStream(fileSource.createInputStream(path)));

		int wasAvailable = in.available();

		try {
			Timer timer = Timer.startNewTimer();

			JavaClass javaClass = JavaClass.read(in, String.valueOf(path.getParent()));

			timer.logElapsed("Class reading");

			return javaClass;

		} catch (DisassemblingException ex) {
			Logger.warningFormatted("At pos 0x" + Integer.toHexString(wasAvailable - in.available()));
			ex.printStackTrace();

			return null;

		} finally {
			in.close();
		}
	}
	
	protected void writeSeparator() {
		if(!separateOutputStream) {
			out.resetIndent().print("\n\n----------------------------------------------------------------------------------------------------\n\n");
		}
	}
}
