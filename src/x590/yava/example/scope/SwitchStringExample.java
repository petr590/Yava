package x590.yava.example.scope;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;
import x590.yava.example.scope.SwitchEnumExample.IConst;

@Example
@SuppressWarnings("unused")
public class SwitchStringExample {

	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(SwitchStringExample.class, "-A");
	}

	public static IConst foo(String s) {
		switch (s) {
			case "iconst_m1":
				return IConst.ICONST_M1;
			case "iconst_0":
				return IConst.ICONST_0;
			case "iconst_1":
				return IConst.ICONST_1;
			case "iconst_2":
				return IConst.ICONST_2;
			case "iconst_3":
				return IConst.ICONST_3;
			case "iconst_4":
				return IConst.ICONST_4;
			case "iconst_5":
				return IConst.ICONST_5;
			default:
				return IConst.ICONST;
		}
	}
}
