package x590.yava.example.assembling;

import x590.yava.example.ExampleTesting;

public class AssemblingExampleTesting extends ExampleTesting {

	public static final AssemblingExampleTesting INSTANCE = new AssemblingExampleTesting();

	private AssemblingExampleTesting() {
		super(".jasm");
	}

	@Override
	public void run(boolean isDebug, String... args) {
		String[] newArgs = new String[args.length + 1];
		System.arraycopy(args, 0, newArgs, 1, args.length);
		newArgs[0] = "-as";
		super.run(isDebug, newArgs);
	}
}
