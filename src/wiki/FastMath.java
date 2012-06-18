package wiki;

import static java.lang.Math.sqrt;

import java.io.PrintStream;

/**
 * FastMath - a faster replacement to Trig
 *
 * Run command:
 *   java -Xmx1G wiki.FastMath
 *
 * @author Rednaxela
 * @author Skilgannon
 * @author Starrynte
 * @author Nat
 */
public final class FastMath {

    public static final double PI        = 3.1415926535897932384626433832795D;
    public static final double TWO_PI    = 6.2831853071795864769252867665590D;
    public static final double HALF_PI   = 1.5707963267948966192313216916398D;

    private static final int TRIG_DIVISIONS = 8192;//MUST be power of 2!!!
    private static final int TRIG_HIGH_DIVISIONS = 131072;//MUST be power of 2!!!
    private static final double K           = TRIG_DIVISIONS / TWO_PI;
    private static final double ACOS_K      = (TRIG_HIGH_DIVISIONS - 1)/ 2;
    private static final double TAN_K      = TRIG_HIGH_DIVISIONS / PI;

    private static final double[] sineTable = new double[TRIG_DIVISIONS];
    private static final double[] tanTable  = new double[TRIG_HIGH_DIVISIONS];
    private static final double[] acosTable = new double[TRIG_HIGH_DIVISIONS];

    static {
        for (int i = 0; i < TRIG_DIVISIONS; i++) {
            sineTable[i] = Math.sin(i/K);
        }
        for(int i = 0; i < TRIG_HIGH_DIVISIONS; i++){
            tanTable[i]  = Math.tan(i/TAN_K);
            acosTable[i] = Math.acos(i / ACOS_K - 1);
        }
    }

    public static double sin(double value) {
        return sineTable[(int)(((value * K + 0.5) % TRIG_DIVISIONS + TRIG_DIVISIONS) )&(TRIG_DIVISIONS - 1)];
    }

    public static double cos(double value) {
        return sineTable[(int)(((value * K + 0.5) % TRIG_DIVISIONS + 1.25 * TRIG_DIVISIONS) )&(TRIG_DIVISIONS - 1)];
    }

    public static double tan(double value) {
        return tanTable[(int)(((value * TAN_K + 0.5) % TRIG_HIGH_DIVISIONS + TRIG_HIGH_DIVISIONS) )&(TRIG_HIGH_DIVISIONS - 1)];
    }

    public static double asin(double value) {
        return HALF_PI - acos(value);
    }

    public static double pow(final double a, final double b) {
        final long tmp = Double.doubleToLongBits(a);
        final long tmp2 = (long)(b * (tmp - 4606921280493453312L)) + 4606921280493453312L;
        return Double.longBitsToDouble(tmp2);
    }

    public static double acos(double value) {
        return acosTable[(int)(value*ACOS_K + (ACOS_K + 0.5))];
    }

    public static double atan(double value) {
        return (value >= 0 ? acos(1 / sqrt(value * value + 1)) : -acos(1 / sqrt(value * value + 1)));
    }

    public static double atan2(double x, double y) {
        return (x >= 0 ? acos(y / sqrt(x*x + y*y)) : -acos(y / sqrt(x*x + y*y)));
    }
}