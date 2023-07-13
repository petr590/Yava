package x590.yava.main.performing;

import x590.yava.FileSource;
import x590.yava.clazz.JavaClass;
import x590.yava.main.Config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.function.Function;

public abstract class AbstractPerforming<S extends OutputStream> implements Performing<S> {

	private final PerformingType type;

	protected S out;

	/** Если {@code true}, то выводится в {@link System#out}, иначе записывается в файлы */
	protected final boolean separateOutputStream;

	protected final FileSource fileSource;

	public AbstractPerforming(PerformingType type, Config config) {
		this(type, config, !config.writeToConsole());
	}

	public AbstractPerforming(PerformingType type, Config config, boolean separateOutputStream) {
		this.type = type;
		this.separateOutputStream = separateOutputStream;
		this.fileSource = config.getFileSource();
	}

	public PerformingType getType() {
		return type;
	}

	@Override
	public void setup() throws UncheckedIOException {
		if (!separateOutputStream)
			this.out = createOutputStream(System.out);
	}

	protected abstract String getOutputFileExtension();

	protected abstract S createOutputStream(OutputStream out);

	@Override
	public S getOutputStream() {
		return out;
	}

	@Override
	public final void write(JavaClass clazz) throws IOException, UncheckedIOException {
		if (separateOutputStream) {
			this.out = createOutputStream(new FileOutputStream(clazz.getFilePath(getOutputFileExtension())));
			doWrite(clazz);
			out.close();
			out = null;
		} else {
			doWrite(clazz);
		}
	}

	public abstract void doWrite(JavaClass clazz);

	@Override
	public void finalizePerforming() {}

	@Override
	public void close() throws IOException, UncheckedIOException {
		if (!separateOutputStream && out != null) {
			out.close();
			out = null;
		}
	}


	public enum PerformingType {
		DECOMPILE(DecompilingPerforming::new),
		DISASSEMBLE(DisassemblingPerforming::new),
		ASSEMBLE(AssemblingPerforming::new);

		private final Function<Config, Performing<?>> performingGetter;

		PerformingType(Function<Config, Performing<?>> performingGetter) {
			this.performingGetter = performingGetter;
		}

		public Performing<?> getPerforming(Config config) {
			return performingGetter.apply(config);
		}
	}
}
