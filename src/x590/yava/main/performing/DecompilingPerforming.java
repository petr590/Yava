package x590.yava.main.performing;

import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import x590.util.Logger;
import x590.util.TextUtil;
import x590.yava.clazz.DecompilationStats;
import x590.yava.clazz.JavaClass;
import x590.yava.io.StringifyOutputStream;
import x590.yava.main.Config;
import x590.yava.main.Yava;
import x590.yava.method.JavaMethod;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Представляет действие декомпиляции класса
 */
public class DecompilingPerforming extends DecodingPerforming<StringifyOutputStream> implements DecompilationStats {

	private final Map<JavaClass, IntObjectPair<List<? extends JavaMethod>>> methodsStat = new LinkedHashMap<>();

	public DecompilingPerforming(Config config) {
		super(config);
	}

	@Override
	protected StringifyOutputStream createOutputStream(OutputStream out) {
		return new StringifyOutputStream(out);
	}

	@Override
	public void perform(JavaClass clazz) {
		clazz.decompile(this);
	}

	@Override
	public void addMethodsStat(JavaClass javaClass, List<? extends JavaMethod> methodsWithExceptions, int totalMethods) {
		methodsStat.put(javaClass, IntObjectImmutablePair.of(totalMethods, methodsWithExceptions));
	}

	@Override
	public void afterPerforming(JavaClass clazz) {
		clazz.afterDecompilation();
		clazz.resolveImports();
	}

	@Override
	public void doWrite(JavaClass clazz) {
		super.writeSeparator();
		clazz.writeTo(out);
		super.writeSeparator();
	}

	@Override
	public void finalizePerforming() {

		Logger.log("Summary:");

		String indent = Yava.getConfig().getIndent();
		String doubleIndent = indent.repeat(2);

		int maxClassNameLength = methodsStat.keySet().stream()
				.mapToInt(javaClass -> javaClass.getThisType().getName().length())
				.max().orElse(0);

		String classFormat = indent + "class %-" + maxClassNameLength + "s: %s decompiled successfully, %s occurred";

		methodsStat.forEach((javaClass, stat) -> {
			var errorMethods = stat.right();
			int errorMethodsCount = errorMethods.size();

			Logger.logf(classFormat, javaClass.getThisType().getName(),
					TextUtil.formatSingularOrPlural(stat.leftInt() - errorMethodsCount, "method", " ", "s"),
					TextUtil.formatSingularOrPlural(errorMethodsCount, "exception", " ", "s"));

			var descriptorsStrings = errorMethods.stream()
					.map(javaMethod -> javaMethod.getDescriptor().toStringIgnoreClass()).toList();

			int maxMethodDescriptorLength =
					descriptorsStrings.stream().mapToInt(String::length).max().orElse(0);

			String methodFormat = doubleIndent + "at %-" + maxMethodDescriptorLength + "s: %s";

			Iterator<String> descriptorsStringsIter = descriptorsStrings.iterator();

			for (var javaMethod : errorMethods) {
				Logger.logf(methodFormat, descriptorsStringsIter.next(), javaMethod.getDecompilationException());
			}
		});
	}
}
