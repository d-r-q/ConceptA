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
    public static int teammates;
    public static String[] teammatesNames;
    public static int myIndex;

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

    public static boolean isTeammate(String name) {
        if (teammatesNames == null) {
            return false;
        }
        for (String teammateName : teammatesNames) {
            if (teammateName.equals(name)) {
                return true;
            }
        }

        return false;
    }

}
