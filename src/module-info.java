module yava {
	
	requires java.base;
	requires transitive x590.util;
	requires transitive x590.argparser;
	requires transitive it.unimi.dsi.fastutil;
	requires junit;

	opens x590.yava.test to junit;
	opens x590.yava.testing to junit;
	opens x590.yava.testing.parsing to junit;
	
	exports x590.yava;
	exports x590.yava.main;
	exports x590.yava.main.performing;
	exports x590.yava.writable;
	
	exports x590.yava.type;
	exports x590.yava.type.primitive;
	exports x590.yava.type.reference;
	exports x590.yava.type.reference.generic;
	exports x590.yava.type.special;
	
	exports x590.yava.clazz;
	exports x590.yava.field;
	exports x590.yava.method;
	exports x590.yava.modifiers;
	exports x590.yava.constpool;
	
	exports x590.yava.attribute;
	exports x590.yava.attribute.annotation;
	exports x590.yava.attribute.signature;
	
	exports x590.yava.context;
	exports x590.yava.variable;
	
	exports x590.yava.instruction;
	exports x590.yava.instruction.load;
	exports x590.yava.instruction.store;
	exports x590.yava.instruction.cast;
	exports x590.yava.instruction.constant;
	exports x590.yava.instruction.field;
	exports x590.yava.instruction.invoke;
	exports x590.yava.instruction.operator;
	exports x590.yava.instruction.array;
	exports x590.yava.instruction.arrayload;
	exports x590.yava.instruction.arraystore;
	exports x590.yava.instruction.cmp;
	exports x590.yava.instruction.scope;
	exports x590.yava.instruction.returning;
	exports x590.yava.instruction.dup;
	exports x590.yava.instruction.other;
	
	exports x590.yava.operation;
	exports x590.yava.operation.load;
	exports x590.yava.operation.store;
	exports x590.yava.operation.cast;
	exports x590.yava.operation.increment;
	exports x590.yava.operation.constant;
	exports x590.yava.operation.field;
	exports x590.yava.operation.invoke;
	exports x590.yava.operation.operator;
	exports x590.yava.operation.array;
	exports x590.yava.operation.arrayload;
	exports x590.yava.operation.arraystore;
	exports x590.yava.operation.variable;
	exports x590.yava.operation.cmp;
	exports x590.yava.operation.condition;
	exports x590.yava.operation.execstream;
	exports x590.yava.operation.returning;
	exports x590.yava.operation.other;
	exports x590.yava.scope;
	
	exports x590.yava.io;
	exports x590.yava.util;
	exports x590.yava.exception;
	exports x590.yava.instruction.increment;
	exports x590.yava.exception.decompilation;
	exports x590.yava.exception.disassembling;
	exports x590.yava.exception.parsing;
}
