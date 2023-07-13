package x590.yava.instruction.binary;

import x590.yava.instruction.Instruction;

/**
 * Является и обычной, и бинарной инструкцией
 */
public interface SimpleInstruction extends Instruction, BinaryInstruction {}
