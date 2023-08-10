package x590.yava.codeanalysis;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import x590.util.Logger;
import x590.yava.clazz.JavaClass;
import x590.yava.constpool.MethodrefConstant;
import x590.yava.instruction.invoke.InvokeInstruction;
import x590.yava.main.Yava;
import x590.yava.main.performing.AbstractPerforming.PerformingType;
import x590.yava.method.JavaMethod;
import x590.yava.method.MethodDescriptor;
import x590.yava.testing.DecompilationTest;
import x590.yava.type.reference.ClassType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CodeAnalysis {

	public static void main(String[] args) {

		checkPermissions(
				DecompilationTest.findAllClassPathsAsStreamInPackage("x590/yava")
						.filter(path -> !path.startsWith("example"))
						.collect(Collectors.toSet())
		);

//		try(InputStream in = ClassLoader.getSystemClassLoader()
//				.getResource("x590/yava/instruction/arraystore/AAStoreInstruction.class")
//				.openStream()) {
//			
//			var classinfo = JavaClass.read(in).getClassInfo();
//			
//			var method = classinfo.getMethod(
//					MethodDescriptor.of(
//							ClassType.fromDescriptor("x590/yava/operation/Operation"),
//							ClassType.fromDescriptor("x590/yava/instruction/arraystore/AAStoreInstruction"),
//							"toOperation",
//							ClassType.fromDescriptor("x590/yava/context/DecompilationContext")
//					)
//			);
//			
//			method.resolveOverrideAnnotation(classinfo);
//			
//			System.out.println(method.hasOverrideAnnotation());
//			
//		} catch(IOException ex) {
//			ex.printStackTrace();
//		}
	}

	private static void checkPermissions(Set<String> classPaths) {

		Yava.init(classPaths.toArray(String[]::new));

		Yava yava = Yava.getInstance();

		List<JavaClass> classes = new ArrayList<>(yava.getFiles().size());

		var classLoader = ClassLoader.getSystemClassLoader();

		var nullStream = new PrintStream(OutputStream.nullOutputStream());

		Logger.setOutputAndErrorStream(nullStream, nullStream);

		for (String file : yava.getFiles()) {

			try (InputStream in = classLoader.getResource(file).openStream()) {

				classes.add(JavaClass.read(in, PerformingType.DECOMPILE));

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		Object2BooleanMap<MethodDescriptor> methodsUsage = new Object2BooleanArrayMap<>();

		for (JavaClass clazz : classes) {

			var classinfo = clazz.getClassInfo();
			var pool = clazz.getConstPool();

			for (JavaMethod method : clazz.getMethods()) {

				if (method.getModifiers().isSynthetic())
					continue;

				var descriptor = method.getDescriptor();

				if (!descriptor.isPlain()) {
					methodsUsage.put(descriptor, true);

				} else if (!methodsUsage.getBoolean(descriptor)) {

					method.resolveOverrideAnnotation(classinfo);
					methodsUsage.put(descriptor, method.hasOverrideAnnotation());
				}

				for (var instruction : method.getDisassemblerContext().getInstructions()) {

					if (instruction instanceof InvokeInstruction invoke &&
							pool.get(invoke.getIndex()) instanceof MethodrefConstant methodref) {

						var invokeDescriptor = methodref.toDescriptor();

						if (invokeDescriptor.getDeclaringClass() instanceof ClassType classType &&
								classType.getPackageName().startsWith("x590.yava")) {

							methodsUsage.put(invokeDescriptor, true);
						}
					}
				}
			}
		}


		for (var entry : methodsUsage.object2BooleanEntrySet()) {
			if (!entry.getBooleanValue()) {
				System.out.println(entry.getKey());
			}
		}
	}
}
