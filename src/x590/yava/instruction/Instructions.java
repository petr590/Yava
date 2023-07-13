package x590.yava.instruction;

import x590.yava.instruction.array.ArrayLoadInstruction;
import x590.yava.instruction.array.ArrayStoreInstruction;
import x590.yava.instruction.cast.CastInstruction;
import x590.yava.instruction.constant.DConstInstruction;
import x590.yava.instruction.constant.FConstInstruction;
import x590.yava.instruction.constant.IConstInstruction;
import x590.yava.instruction.constant.LConstInstruction;
import x590.yava.instruction.binary.SimpleInstruction;
import x590.yava.instruction.load.*;
import x590.yava.instruction.operator.*;
import x590.yava.instruction.other.SimpleOperationInstruction;
import x590.yava.instruction.other.StackChangingInstruction;
import x590.yava.instruction.other.PopInstruction;
import x590.yava.instruction.store.*;
import x590.yava.instruction.constant.AConstNullInstruction;
import x590.yava.operation.array.*;
import x590.yava.operation.cmp.DCmpOperation;
import x590.yava.operation.cmp.FCmpOperation;
import x590.yava.operation.cmp.LCmpOperation;
import x590.yava.operation.other.AThrowOperation;
import x590.yava.operation.other.StackChanging;
import x590.yava.operation.returning.*;
import x590.yava.scope.SynchronizedScope;
import x590.yava.type.TypeSize;
import x590.yava.type.reference.ArrayType;

import static x590.yava.type.primitive.PrimitiveType.*;

public final class Instructions {

	private Instructions() {}

	public static final SimpleInstruction
			ACONST_NULL = AConstNullInstruction.INSTANCE,

			ICONST_M1 = IConstInstruction.of(-1),
			ICONST_0 = IConstInstruction.of(0),
			ICONST_1 = IConstInstruction.of(1),
			ICONST_2 = IConstInstruction.of(2),
			ICONST_3 = IConstInstruction.of(3),
			ICONST_4 = IConstInstruction.of(4),
			ICONST_5 = IConstInstruction.of(5),

			LCONST_0 = new LConstInstruction(0),
			LCONST_1 = new LConstInstruction(1),

			FCONST_0 = new FConstInstruction(0),
			FCONST_1 = new FConstInstruction(1),
			FCONST_2 = new FConstInstruction(2),

			DCONST_0 = new DConstInstruction(0),
			DCONST_1 = new DConstInstruction(1),


			ILOAD_0 = ILoadInstruction.of(0),
			ILOAD_1 = ILoadInstruction.of(1),
			ILOAD_2 = ILoadInstruction.of(2),
			ILOAD_3 = ILoadInstruction.of(3),

			LLOAD_0 = LLoadInstruction.of(0),
			LLOAD_1 = LLoadInstruction.of(1),
			LLOAD_2 = LLoadInstruction.of(2),
			LLOAD_3 = LLoadInstruction.of(3),

			FLOAD_0 = FLoadInstruction.of(0),
			FLOAD_1 = FLoadInstruction.of(1),
			FLOAD_2 = FLoadInstruction.of(2),
			FLOAD_3 = FLoadInstruction.of(3),

			DLOAD_0 = DLoadInstruction.of(0),
			DLOAD_1 = DLoadInstruction.of(1),
			DLOAD_2 = DLoadInstruction.of(2),
			DLOAD_3 = DLoadInstruction.of(3),

			ALOAD_0 = ALoadInstruction.of(0),
			ALOAD_1 = ALoadInstruction.of(1),
			ALOAD_2 = ALoadInstruction.of(2),
			ALOAD_3 = ALoadInstruction.of(3),


			ISTORE_0 = IStoreInstruction.of(0),
			ISTORE_1 = IStoreInstruction.of(1),
			ISTORE_2 = IStoreInstruction.of(2),
			ISTORE_3 = IStoreInstruction.of(3),

			LSTORE_0 = LStoreInstruction.of(0),
			LSTORE_1 = LStoreInstruction.of(1),
			LSTORE_2 = LStoreInstruction.of(2),
			LSTORE_3 = LStoreInstruction.of(3),

			FSTORE_0 = FStoreInstruction.of(0),
			FSTORE_1 = FStoreInstruction.of(1),
			FSTORE_2 = FStoreInstruction.of(2),
			FSTORE_3 = FStoreInstruction.of(3),

			DSTORE_0 = DStoreInstruction.of(0),
			DSTORE_1 = DStoreInstruction.of(1),
			DSTORE_2 = DStoreInstruction.of(2),
			DSTORE_3 = DStoreInstruction.of(3),

			ASTORE_0 = AStoreInstruction.of(0),
			ASTORE_1 = AStoreInstruction.of(1),
			ASTORE_2 = AStoreInstruction.of(2),
			ASTORE_3 = AStoreInstruction.of(3),


			IALOAD = new ArrayLoadInstruction("iaload", IALoadOperation::new),
			LALOAD = new ArrayLoadInstruction("laload", context -> new ArrayLoadOperation(ArrayType.LONG_ARRAY, context)),
			FALOAD = new ArrayLoadInstruction("faload", context -> new ArrayLoadOperation(ArrayType.FLOAT_ARRAY, context)),
			DALOAD = new ArrayLoadInstruction("daload", context -> new ArrayLoadOperation(ArrayType.DOUBLE_ARRAY, context)),
			AALOAD = new ArrayLoadInstruction("aaload", context -> new ArrayLoadOperation(ArrayType.ANY_OBJECT_ARRAY, context)),
			BALOAD = new ArrayLoadInstruction("baload", context -> new ArrayLoadOperation(ArrayType.BYTE_OR_BOOLEAN_ARRAY, context)),
			CALOAD = new ArrayLoadInstruction("caload", context -> new ArrayLoadOperation(ArrayType.CHAR_ARRAY, context)),
			SALOAD = new ArrayLoadInstruction("saload", context -> new ArrayLoadOperation(ArrayType.SHORT_ARRAY, context)),

			IASTORE = new ArrayStoreInstruction("iastore", IAStoreOperation::new),
			LASTORE = new ArrayStoreInstruction("lastore", context -> new ArrayStoreOperation(ArrayType.LONG_ARRAY, context)),
			FASTORE = new ArrayStoreInstruction("fastore", context -> new ArrayStoreOperation(ArrayType.FLOAT_ARRAY, context)),
			DASTORE = new ArrayStoreInstruction("dastore", context -> new ArrayStoreOperation(ArrayType.DOUBLE_ARRAY, context)),
			AASTORE = new ArrayStoreInstruction("aastore", context -> new ArrayStoreOperation(ArrayType.ANY_OBJECT_ARRAY, context)),
			BASTORE = new ArrayStoreInstruction("bastore", context -> new ArrayStoreOperation(ArrayType.BYTE_OR_BOOLEAN_ARRAY, context)),
			CASTORE = new ArrayStoreInstruction("castore", context -> new ArrayStoreOperation(ArrayType.CHAR_ARRAY, context)),
			SASTORE = new ArrayStoreInstruction("sastore", context -> new ArrayStoreOperation(ArrayType.SHORT_ARRAY, context)),


			POP  = new PopInstruction("pop", TypeSize.WORD),
			POP2 = new PopInstruction("pop2", TypeSize.LONG),
			DUP     = new StackChangingInstruction("dup",     StackChanging::dup),
			DUP_X1  = new StackChangingInstruction("dup_x1",  StackChanging::dupX1),
			DUP_X2  = new StackChangingInstruction("dup_x2",  StackChanging::dupX2),
			DUP2    = new StackChangingInstruction("dup2",    StackChanging::dup2),
			DUP2_X1 = new StackChangingInstruction("dup2_x1", StackChanging::dup2X1),
			DUP2_X2 = new StackChangingInstruction("dup2_x2", StackChanging::dup2X2),
			SWAP    = new StackChangingInstruction("swap",    StackChanging::swap),


			IADD = new AddOperatorInstruction("iadd", INT),
			LADD = new AddOperatorInstruction("ladd", LONG),
			FADD = new AddOperatorInstruction("fadd", FLOAT),
			DADD = new AddOperatorInstruction("dadd", DOUBLE),
			ISUB = new SubOperatorInstruction("isub", INT),
			LSUB = new SubOperatorInstruction("lsub", LONG),
			FSUB = new SubOperatorInstruction("fsub", FLOAT),
			DSUB = new SubOperatorInstruction("dsub", DOUBLE),
			IMUL = new MulOperatorInstruction("imul", INT),
			LMUL = new MulOperatorInstruction("lmul", LONG),
			FMUL = new MulOperatorInstruction("fmul", FLOAT),
			DMUL = new MulOperatorInstruction("dmul", DOUBLE),
			IDIV = new DivOperatorInstruction("idiv", INT),
			LDIV = new DivOperatorInstruction("ldiv", LONG),
			FDIV = new DivOperatorInstruction("fdiv", FLOAT),
			DDIV = new DivOperatorInstruction("ddiv", DOUBLE),
			IREM = new RemOperatorInstruction("irem", INT),
			LREM = new RemOperatorInstruction("lrem", LONG),
			FREM = new RemOperatorInstruction("frem", FLOAT),
			DREM = new RemOperatorInstruction("drem", DOUBLE),
			INEG = new NegOperatorInstruction("ineg", INT),
			LNEG = new NegOperatorInstruction("lneg", LONG),
			FNEG = new NegOperatorInstruction("fneg", FLOAT),
			DNEG = new NegOperatorInstruction("dneg", DOUBLE),

			ISHL = new ShiftLeftOperatorInstruction("ishl", INT),
			LSHL = new ShiftLeftOperatorInstruction("lshl", LONG),
			ISHR = new ShiftRightOperatorInstruction("ishr", INT),
			LSHR = new ShiftRightOperatorInstruction("lshr", LONG),
			IUSHR = new UShiftRightOperatorInstruction("iushr", INT),
			LUSHR = new UShiftRightOperatorInstruction("lushr", LONG),
			IAND = new AndOperatorInstruction("iand", INT_BOOLEAN),
			LAND = new AndOperatorInstruction("land", LONG),
			IOR = new OrOperatorInstruction("ior", INT_BOOLEAN),
			LOR = new OrOperatorInstruction("lor", LONG),
			IXOR = new XorOperatorInstruction("ixor", INT_BOOLEAN),
			LXOR = new XorOperatorInstruction("lxor", LONG),


			I2L = new CastInstruction("i2l", INT, LONG, true),
			I2F = new CastInstruction("i2f", INT, FLOAT, true),
			I2D = new CastInstruction("i2d", INT, DOUBLE, true),
			L2I = new CastInstruction("l2i", LONG, INT, false),
			L2F = new CastInstruction("l2f", LONG, FLOAT, true),
			L2D = new CastInstruction("l2d", LONG, DOUBLE, true),
			F2I = new CastInstruction("f2i", FLOAT, INT, false),
			F2L = new CastInstruction("f2l", FLOAT, LONG, false),
			F2D = new CastInstruction("f2d", FLOAT, DOUBLE, true),
			D2I = new CastInstruction("d2i", DOUBLE, INT, false),
			D2L = new CastInstruction("d2l", DOUBLE, LONG, false),
			D2F = new CastInstruction("d2f", DOUBLE, FLOAT, false),
			I2B = new CastInstruction("i2b", INT, BYTE, false),
			I2C = new CastInstruction("i2c", INT, CHAR, false),
			I2S = new CastInstruction("i2s", INT, SHORT, false),


			LCMP  = new SimpleOperationInstruction<>("lcmp",  LCmpOperation::new),
			FCMPL = new SimpleOperationInstruction<>("fcmpl", FCmpOperation::new),
			FCMPG = new SimpleOperationInstruction<>("fcmpg", FCmpOperation::new),
			DCMPL = new SimpleOperationInstruction<>("dcmpl", DCmpOperation::new),
			DCMPG = new SimpleOperationInstruction<>("dcmpg", DCmpOperation::new),


			IRETURN = new SimpleOperationInstruction<>("ireturn", IReturnOperation::new),
			LRETURN = new SimpleOperationInstruction<>("lreturn", LReturnOperation::new),
			FRETURN = new SimpleOperationInstruction<>("freturn", FReturnOperation::new),
			DRETURN = new SimpleOperationInstruction<>("dreturn", DReturnOperation::new),
			ARETURN = new SimpleOperationInstruction<>("areturn", AReturnOperation::new),
			VRETURN = new SimpleOperationInstruction<>("return",  VReturnOperation::getInstance),

			ARRAYLENGTH = new SimpleOperationInstruction<>("arraylength", ArrayLengthOperation::new),
			ATHROW      = new SimpleOperationInstruction<>("athrow", AThrowOperation::new),

			MONITORENTER = new SimpleOperationInstruction<>("monitorenter", SynchronizedScope::new),
			MONITOREXIT  = new SimpleOperationInstruction<>("monitorexit", SynchronizedScope::monitorExit);
}
