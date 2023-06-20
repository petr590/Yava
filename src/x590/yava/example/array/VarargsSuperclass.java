package x590.yava.example.array;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class VarargsSuperclass {

	static void foo() {
	}

	static void foo(double arg1) {
	}

	static void foo(double arg1, double arg2) {
	}

	static void foo(double arg1, double arg2, double arg3) {
	}

	static void foo(double... args) {
	}

	static void foo2(String name, Method... methods) {
	}

	static void foo3(String name, Method[] methods) {
	}
}
