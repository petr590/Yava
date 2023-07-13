package x590.yava.example;

public class DisassemblingExampleTesting extends DecodingExampleTesting {

	public static final DisassemblingExampleTesting INSTANCE = new DisassemblingExampleTesting();

	private DisassemblingExampleTesting() {
		super(".class");
	}

	@Override
	public void run(boolean isDebug, String... args) {
		String[] newArgs = new String[args.length + 1];
		System.arraycopy(args, 0, newArgs, 1, args.length);
		newArgs[0] = "-ds";
		super.run(isDebug, newArgs);
	}
}
