package lxx.util;

import lxx.BattleConstants;
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

    public static double angle(CaPoint base, CaPoint to) {
        return FastMath.atan2(to.x - base.x, to.y - base.y);
    }

    public static double anglesDiff(double angle1, double angle2) {
        return abs(Utils.normalRelativeAngle(angle1 - angle2));
    }

    public static double getRobotWidthInRadians(CaPoint center, CaPoint robotPos) {
        return getRobotWidthInRadians(angle(center, robotPos), center.distance(robotPos));
    }

    public static double getRobotWidthInRadians(double angle, double distance) {
        final double alpha = abs(CaConstants.RADIANS_45 - (angle % CaConstants.RADIANS_90));
        if (distance < BattleConstants.robotDiagonal) {
            distance = BattleConstants.robotDiagonal;
        }
        return FastMath.asin(FastMath.cos(alpha) * BattleConstants.robotDiagonal / distance);
    }

}
