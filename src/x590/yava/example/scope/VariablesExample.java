package x590.yava.example.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import x590.yava.example.Example;
import x590.yava.example.ExampleTesting;

@Example
@SuppressWarnings("unused")
public class VariablesExample {
	
	public static void main(String[] args) {
		ExampleTesting.DECOMPILING.run(VariablesExample.class);
	}
	
	public static void foo(int a, int b) {
		
		List<Object> objs = new ArrayList<>();
		objs.add(new Object());
		
		if(a == 0) {
			Map<Object, Object> map = new HashMap<>();
			
			if(b == 0)
				map.put(objs.get(0), "a");
		}
	}
}
