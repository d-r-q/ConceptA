package lxx.radar;

import lxx.model.CaRobot;
import lxx.util.CaConstants;
import robocode.util.Utils;

import static java.lang.Math.signum;

/**
 * User: Aleksey Zhidkov
 * Date: 27.06.12
 */
public class DuelRadar {

    private DuelRadar() {
    }

    public static double getRadarTurnAngleRadians(CaRobot me, CaRobot duelOpponent) {
        final double angleToTarget = me.angleTo(duelOpponent);
        final double sign = (angleToTarget != me.getRadarHeading())
                ? signum(Utils.normalRelativeAngle(angleToTarget - me.getRadarHeading()))
                : 1;

        return Utils.normalRelativeAngle(angleToTarget - me.getRadarHeading() + CaConstants.RADIANS_5 * sign);
    }

}
