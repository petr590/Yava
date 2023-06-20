package x590.yava.example.invoke;

public class ConcatStringsExample {

	public static Object[] staticStrs = {};
	public Object[] nonstaticStrs = {};

	public void foo(int i, int j) {
		String str = "i: {" + i + "}; j: {" + j + "}";
		str += str;

		staticStrs[0] = staticStrs[0] + String.valueOf(i);
		nonstaticStrs[0] = nonstaticStrs[0] + String.valueOf(j);

		staticStrs[0] += String.valueOf(i);
		nonstaticStrs[0] += String.valueOf(j);
	}
}
