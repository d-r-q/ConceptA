package lxx.model;

import lxx.BattleConstants;
import lxx.util.CaPoint;
import robocode.*;

import java.util.List;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaRobotStateFactory {

    public static CaRobotState getMyState(List<Event> events) {
        RobotStatus status = null;
        long time = -1;
        DeathEvent de = null;
        for (Event e : events) {
            if (e instanceof StatusEvent) {
                final StatusEvent statusEvent = (StatusEvent) e;
                status = statusEvent.getStatus();
                time = e.getTime();
            } else if (e instanceof DeathEvent) {
                de = (DeathEvent) e;
            }
        }

        if (status == null) {
            throw new RuntimeException("Something wrong");
        }

        return new CaRobotState(BattleConstants.myName, new CaPoint(status.getX(), status.getY()), status.getVelocity(),
                status.getHeadingRadians(), status.getEnergy(), time, de != null, status.getRadarHeadingRadians(), status.getGunHeadingRadians());
    }

    public static CaRobotState getAnotherRobotState(CaRobot me, CaRobot prevState, List<Event> events) {
        CaPoint pos = prevState.position;
        double velocity = prevState.velocity;
        double heading = prevState.heading;
        double energy = prevState.energy;
        boolean alive = true;

        for (Event e : events) {
            if (e instanceof RobotDeathEvent) {
                alive = false;
            } else if (e instanceof ScannedRobotEvent) {
                final ScannedRobotEvent se = (ScannedRobotEvent) e;
                pos = me.getPosition(). project(me.getHeading() + se.getBearingRadians(), se.getDistance());
                velocity = se.getVelocity();
                heading = se.getHeadingRadians();
                energy = se.getEnergy();
            }
        }

        return new CaRobotState(prevState.getName(), pos, velocity, heading, energy, events.get(0).getTime(), alive);
    }

}
