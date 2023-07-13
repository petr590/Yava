package x590.yava.instruction.binary;

import x590.yava.attribute.code.BytecodeDisassemblingContext;
import x590.yava.writable.DisassemblingWritable;

/**
 * Инструкция, хранящая только имя и бинарные данные.
 * Используется для записи в .jasm файлы в виде дизассемблированного байткода
 */
public interface BinaryInstruction extends DisassemblingWritable<BytecodeDisassemblingContext> {}
