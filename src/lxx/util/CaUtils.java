package lxx.util;

import lxx.BattleConstants;
import lxx.model.CaRobot;
import robocode.Rules;
import robocode.util.Utils;
import wiki.FastMath;

import java.awt.geom.Point2D;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaUtils {

    private static final double DOUBLE_PI = Math.PI * 2;
    private static final double HALF_PI = Math.PI / 2;

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
        // todo: fix me
        double theta = FastMath.asin((y - baseY) / Point2D.distance(x, y, baseX, baseY)) - HALF_PI;
        if (x >= baseX && theta < 0) {
            theta = -theta;
        }
        return (theta %= DOUBLE_PI) >= 0 ? theta : (theta + DOUBLE_PI);
    }

    public static double angle(CaPoint base, CaPoint to) {
        return angle(base.x, base.y, to.x, to.y);
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

    public static double getMaxEscapeAngle(double bulletSpeed) {
        return FastMath.asin(Rules.MAX_VELOCITY / bulletSpeed);
    }

    public static double getNonZeroLateralDirection(CaPoint center, CaRobot robot) {
        final double heading;
        final double speed;
        if (robot.getSpeed() < 0.5) {
            heading = robot.getHeading();
            speed = 1;
        } else {
            heading = robot.getMovementDirection();
            speed = robot.getSpeed();
        }

        return signum(lateralVelocity(center, robot.getPosition(), speed, heading));
    }

    private static double lateralVelocity(CaPoint center, CaPoint pos, double velocity, double heading) {
        return velocity * Math.sin(Utils.normalRelativeAngle(heading - center.angleTo(pos)));
    }

}
