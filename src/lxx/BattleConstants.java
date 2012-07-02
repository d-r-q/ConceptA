package lxx;

import lxx.util.CaPoint;
import wiki.FastMath;

import static java.lang.Math.abs;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class BattleConstants {

    public static String myName;
    public static double initialGunHeat;
    public static double gunCoolingRate;
    public static double robotWidth;
    public static double robotHeight;
    public static double robotDiagonal;
    public static int totalEnemies;

    private BattleConstants() {
    }

    public static boolean isRobotContains(CaPoint robotPos, CaPoint pnt) {
        return abs(robotPos.x - pnt.x) < robotWidth / 2 && abs(robotPos.y - pnt.y) < robotWidth / 2;
    }

    public static void setRobotBounds(double width, double height) {
        robotWidth = width;
        robotHeight = height;
        robotDiagonal = Math.sqrt(FastMath.pow(width, 2) + FastMath.pow(height, 2));
    }
}
