package example;

public class StaticFieldsLoopIncrement {

    public static void main(String[] args) {
        ExampleTesting.runDecompiler(StaticFieldsLoopIncrement.class);
    }
    
    static byte b = 0;
    static short s = 0;
    static char c = '\0';
    static int i = 0;
    static long l = 0;
    static float f = 0;
    static double d = 0;
    
    public static void inc() {
        while(b < 1) b++;
        while(s < 1) s++;
        while(c < 1) c++;
        while(i < 1) i++;
        while(l < 1) l++;
        while(f < 1) f++;
        while(d < 1) d++;
    }

    public static void dec() {
        while(b < 1) b--;
        while(s < 1) s--;
        while(c < 1) c--;
        while(i < 1) i--;
        while(l < 1) l--;
        while(f < 1) f--;
        while(d < 1) d--;
    }

    public static void preInc() {
        while(b < 1) ++b;
        while(s < 1) ++s;
        while(c < 1) ++c;
        while(i < 1) ++i;
        while(l < 1) ++l;
        while(f < 1) ++f;
        while(d < 1) ++d;
    }

    public static void preDec() {
        while(b < 1) --b;
        while(s < 1) --s;
        while(c < 1) --c;
        while(i < 1) --i;
        while(l < 1) --l;
        while(f < 1) --f;
        while(d < 1) --d;
    }

    public static void add() {
        while(b < 1) b += b;
        while(s < 1) s += s;
        while(c < 1) c += c;
        while(i < 1) i += i;
        while(l < 1) l += l;
        while(f < 1) f += f;
        while(d < 1) d += d;
    }

    public static void sub() {
        while(b < 1) b -= b;
        while(s < 1) s -= s;
        while(c < 1) c -= c;
        while(i < 1) i -= i;
        while(l < 1) l -= l;
        while(f < 1) f -= f;
        while(d < 1) d -= d;
    }

    public static void mul() {
        while(b < 1) b *= b;
        while(s < 1) s *= s;
        while(c < 1) c *= c;
        while(i < 1) i *= i;
        while(l < 1) l *= l;
        while(f < 1) f *= f;
        while(d < 1) d *= d;
    }

    public static void div() {
        while(b < 1) b /= b;
        while(s < 1) s /= s;
        while(c < 1) c /= c;
        while(i < 1) i /= i;
        while(l < 1) l /= l;
        while(f < 1) f /= f;
        while(d < 1) d /= d;
    }

    public static void rem() {
        while(b < 1) b %= b;
        while(s < 1) s %= s;
        while(c < 1) c %= c;
        while(i < 1) i %= i;
        while(l < 1) l %= l;
        while(f < 1) f %= f;
        while(d < 1) d %= d;
    }

    public static void and() {
        while(b < 1) b &= b;
        while(s < 1) s &= s;
        while(c < 1) c &= c;
        while(i < 1) i &= i;
        while(l < 1) l &= l;
    }

    public static void or() {
        while(b < 1) b |= b;
        while(s < 1) s |= s;
        while(c < 1) c |= c;
        while(i < 1) i |= i;
        while(l < 1) l |= l;
    }

    public static void xor() {
        while(b < 1) b ^= b;
        while(s < 1) s ^= s;
        while(c < 1) c ^= c;
        while(i < 1) i ^= i;
        while(l < 1) l ^= l;
    }

    public static void not() {
        while(b < 1) b = (byte)~b;
        while(s < 1) s = (short)~s;
        while(c < 1) c = (char)~c;
        while(i < 1) i = ~i;
        while(l < 1) l = ~l;
    }

    public static void neg() {
        while(b < 1) b = (byte)-b;
        while(s < 1) s = (short)-s;
        while(c < 1) c = (char)-c;
        while(i < 1) i = -i;
        while(l < 1) l = -l;
    }

    public static void shl() {
        while(b < 1) b <<= b;
        while(s < 1) s <<= s;
        while(c < 1) c <<= c;
        while(i < 1) i <<= i;
        while(l < 1) l <<= (int)l;
    }

    public static void shr() {
        while(b < 1) b >>= b;
        while(s < 1) s >>= s;
        while(c < 1) c >>= c;
        while(i < 1) i >>= i;
        while(l < 1) l >>= (int)l;
    }

    public static void ushr() {
        while(b < 1) b >>>= b;
        while(s < 1) s >>>= s;
        while(c < 1) c >>>= c;
        while(i < 1) i >>>= i;
        while(l < 1) l >>>= (int)l;
    }

    public static void assign() {
        while(b < 1) b = -1;
        while(s < 1) s = -1;
        while(c < 1) c = '\uFFFF';
        while(i < 1) i = -1;
        while(l < 1) l = -1;
        while(f < 1) f = -1;
        while(d < 1) d = -1;
    }
}