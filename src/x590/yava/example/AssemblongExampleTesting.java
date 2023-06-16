package x590.yava.example;

public class AssemblongExampleTesting extends ExampleTesting {

	public static final AssemblongExampleTesting INSTANCE = new AssemblongExampleTesting();

	private AssemblongExampleTesting() {
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
