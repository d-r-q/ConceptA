package lxx.util;

import robocode.util.Utils;
import wiki.FastMath;

import static java.lang.Math.abs;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaUtils {

    public static double limit(double minValue, double value, double maxValue) {
        if (value < minValue) {
            return minValue;
        }

        if (value > maxValue) {
            return maxValue;
        }

        return value;
    }

    public static double angle(double baseX, double baseY, double x, double y) {
        return FastMath.atan2(x - baseX, y - baseY);
    }

    public static double anglesDiff(double angle1, double angle2) {
        return abs(Utils.normalRelativeAngle(angle1 - angle2));
    }
}
