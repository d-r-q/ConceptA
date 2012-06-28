package lxx.model;

import lxx.util.CaConstants;
import lxx.util.CaPoint;
import lxx.util.IntervalDouble;
import robocode.util.Utils;
import wiki.FastMath;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.max;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class BattleField {

    public static final double WALL_STICK = 140;

    public static CaPoint availableLeftBottom;
    public static CaPoint availableLeftTop;
    public static CaPoint availableRightTop;
    public static CaPoint availableRightBottom;

    private static CaPoint leftTop;
    private static CaPoint rightTop;
    private static CaPoint rightBottom;

    public static Wall bottom;
    public static Wall left;
    public static Wall top;
    public static Wall right;

    public static int availableBottomY;
    public static int availableTopY;
    public static int availableLeftX;
    public static int availableRightX;

    public static Rectangle2D.Double availableBattleFieldRectangle;

    public static IntervalDouble noSmoothX;
    public static IntervalDouble noSmoothY;

    public static CaPoint center;

    public static int width;
    public static int height;
    public static double diagonal;

    public static void init(int x, int y, int width, int height) {
        availableBottomY = y;
        availableTopY = y + height;
        availableLeftX = x;
        availableRightX = x + width;

        availableLeftBottom = new CaPoint(availableLeftX, availableBottomY);
        availableLeftTop = new CaPoint(availableLeftX, availableTopY);
        availableRightTop = new CaPoint(availableRightX, availableTopY);
        availableRightBottom = new CaPoint(availableRightX, availableBottomY);

        final int bottomY = 0;
        final int topY = y * 2 + height;
        final int leftX = 0;
        final int rightX = x * 2 + width;

        leftTop = new CaPoint(leftX, topY);
        rightTop = new CaPoint(rightX, topY);
        rightBottom = new CaPoint(rightX, bottomY);

        bottom = new Wall(WallType.BOTTOM, availableRightBottom, availableLeftBottom);
        left = new Wall(WallType.LEFT, availableLeftBottom, availableLeftTop);
        top = new Wall(WallType.TOP, availableLeftTop, availableRightTop);
        right = new Wall(WallType.RIGHT, availableRightTop, availableRightBottom);
        bottom.clockwiseWall = left;
        bottom.counterClockwiseWall = right;
        left.clockwiseWall = top;
        left.counterClockwiseWall = bottom;
        top.clockwiseWall = right;
        top.counterClockwiseWall = left;
        right.clockwiseWall = bottom;
        right.counterClockwiseWall = top;

        availableBattleFieldRectangle = new Rectangle2D.Double(x - 1, y - 1, width + 2, height + 2);

        center = new CaPoint(rightX / 2, topY / 2);

        BattleField.width = width;
        BattleField.height = height;

        noSmoothX = new IntervalDouble(WALL_STICK, width - WALL_STICK);
        noSmoothY = new IntervalDouble(WALL_STICK, height - WALL_STICK);

        diagonal = Point2D.distance(0, 0, width, height);
    }

    // this method is called very often, so keep it optimal
    public static Wall getWall(CaPoint pos, double heading) {
        final double normalHeadingTg = FastMath.tan(heading % CaConstants.RADIANS_90);
        if (heading < CaConstants.RADIANS_90) {
            final double rightTopTg = (rightTop.x - pos.x) / (rightTop.y - pos.y);
            if (normalHeadingTg < rightTopTg) {
                return top;
            } else {
                return right;
            }
        } else if (heading < CaConstants.RADIANS_180) {
            final double rightBottomTg = pos.y / (rightBottom.x - pos.x);
            if (normalHeadingTg < rightBottomTg) {
                return right;
            } else {
                return bottom;
            }
        } else if (heading < CaConstants.RADIANS_270) {
            final double leftBottomTg = pos.x / pos.y;
            if (normalHeadingTg < leftBottomTg) {
                return bottom;
            } else {
                return left;
            }
        } else if (heading < CaConstants.RADIANS_360) {
            final double leftTopTg = (leftTop.y - pos.y) / pos.x;
            if (normalHeadingTg < leftTopTg) {
                return left;
            } else {
                return top;
            }
        }
        throw new IllegalArgumentException("Invalid heading: " + heading);
    }

    public static double getBearingOffsetToWall(CaPoint pnt, double heading) {
        return Utils.normalRelativeAngle(getWall(pnt, heading).wallType.fromCenterAngle - heading);
    }

    public static double getDistanceToWall(Wall wall, CaPoint pnt) {
        switch (wall.wallType) {
            case TOP:
                return availableTopY - pnt.y;
            case RIGHT:
                return availableRightX - pnt.x;
            case BOTTOM:
                return pnt.y - availableBottomY;
            case LEFT:
                return pnt.x - availableLeftX;
            default:
                throw new IllegalArgumentException("Unknown wallType: " + wall.wallType);
        }
    }

    public static double smoothWalls(CaPoint pnt, double desiredHeading, boolean isClockwise) {
        return smoothWall(getWall(pnt, desiredHeading), pnt, desiredHeading, isClockwise);
    }

    private static double smoothWall(Wall wall, CaPoint pnt, double desiredHeading, boolean isClockwise) {
        final double adjacentLeg = max(0, getDistanceToWall(wall, pnt) - 4);
        if (WALL_STICK < adjacentLeg) {
            return desiredHeading;
        }
        double smoothAngle;
        smoothAngle = (FastMath.acos(adjacentLeg / WALL_STICK) + CaConstants.RADIANS_4) * (isClockwise ? 1 : -1);
        final double baseAngle = wall.wallType.fromCenterAngle;
        double smoothedAngle = Utils.normalAbsoluteAngle(baseAngle + smoothAngle);
        final Wall secondWall = isClockwise ? wall.clockwiseWall : wall.counterClockwiseWall;
        return smoothWall(secondWall, pnt, smoothedAngle, isClockwise);
    }

    public static boolean contains(CaPoint point) {
        return availableBattleFieldRectangle.contains(point.getX(), point.getY());
    }

    public static double getDistanceToWall(CaPoint position, double absoluteHeading) {
        return getDistanceToWall(getWall(position, absoluteHeading), position);
    }

    public static class Wall {

        public final WallType wallType;
        public final CaPoint ccw;
        public final CaPoint cw;

        private Wall clockwiseWall;
        private Wall counterClockwiseWall;

        private Wall(WallType wallType, CaPoint ccw, CaPoint cw) {
            this.wallType = wallType;
            this.ccw = ccw;
            this.cw = cw;
        }
    }

    public static enum WallType {

        TOP(CaConstants.RADIANS_0, CaConstants.RADIANS_90, CaConstants.RADIANS_270),
        RIGHT(CaConstants.RADIANS_90, CaConstants.RADIANS_180, CaConstants.RADIANS_0),
        BOTTOM(CaConstants.RADIANS_180, CaConstants.RADIANS_270, CaConstants.RADIANS_90),
        LEFT(CaConstants.RADIANS_270, CaConstants.RADIANS_0, CaConstants.RADIANS_180);

        public final double fromCenterAngle;

        public final double clockwiseAngle;
        public final double counterClockwiseAngle;

        private WallType(double fromCenterAngle, double clockwiseAngle, double counterClockwiseAngle) {
            this.fromCenterAngle = fromCenterAngle;


            this.clockwiseAngle = clockwiseAngle;
            this.counterClockwiseAngle = counterClockwiseAngle;
        }

    }
}
