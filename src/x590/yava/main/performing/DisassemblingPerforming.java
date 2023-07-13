package x590.yava.main.performing;

import x590.yava.clazz.JavaClass;
import x590.yava.io.DisassemblingOutputStream;
import x590.yava.main.Config;

import java.io.OutputStream;

public class DisassemblingPerforming extends DecodingPerforming<DisassemblingOutputStream> {

	public DisassemblingPerforming(Config config) {
		super(PerformingType.DISASSEMBLE, config);
	}

	@Override
	protected String getOutputFileExtension() {
		return ".jasm";
	}

	@Override
	protected DisassemblingOutputStream createOutputStream(OutputStream out) {
		return new DisassemblingOutputStream(out);
	}

	@Override
	public void perform(JavaClass clazz) {}

	@Override
	public void afterPerforming(JavaClass clazz) {}

	@Override
	public void doWrite(JavaClass clazz) {
		super.writeSeparator();
		clazz.writeDisassembled(out);
		super.writeSeparator();
	}
}
