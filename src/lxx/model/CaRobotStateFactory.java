package lxx.model;

import lxx.BattleConstants;
import lxx.util.CaPoint;
import robocode.RobotStatus;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaRobotStateFactory {

    public static CaRobotState createState(StatusEvent e) {

        final RobotStatus status = e.getStatus();
        return new CaRobotState(BattleConstants.myName, new CaPoint(status.getX(), status.getY()), status.getVelocity(),
                status.getHeadingRadians(), status.getEnergy(), e.getTime(), status.getRadarHeadingRadians(), status.getGunHeadingRadians());
    }

    public static CaRobotState createState(StatusEvent e, ScannedRobotEvent se) {
        final RobotStatus status = e.getStatus();

        final CaPoint enemyPos = new CaPoint(status.getX(), status.getY()).
                project(status.getHeadingRadians() + se.getBearingRadians(), se.getDistance());

        return new CaRobotState(se.getName(), enemyPos, se.getVelocity(), se.getHeadingRadians(), se.getEnergy(), se.getTime());
    }

}
