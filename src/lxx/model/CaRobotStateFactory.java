package lxx.model;

import lxx.BattleConstants;
import lxx.events.FireEvent;
import lxx.util.CaConstants;
import lxx.util.CaPoint;
import robocode.*;

import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaRobotStateFactory {

    public static CaRobotState getMyState(List<Event> events) {
        RobotStatus status = null;
        long time = -1;
        DeathEvent de = null;
        double firePower = 0;
        for (Event e : events) {
            if (e instanceof StatusEvent) {
                final StatusEvent statusEvent = (StatusEvent) e;
                status = statusEvent.getStatus();
                time = e.getTime();
            } else if (e instanceof DeathEvent) {
                de = (DeathEvent) e;
            } else if (e instanceof FireEvent) {
                firePower = ((FireEvent) e).bullet.getPower();
            }
        }

        if (status == null) {
            throw new RuntimeException("Something wrong");
        }

        return new CaRobotState(BattleConstants.myName, new CaPoint(status.getX(), status.getY()), status.getVelocity(),
                status.getHeadingRadians(), status.getEnergy(), time, de != null, firePower, status.getGunHeat(),
                status.getRadarHeadingRadians(), status.getGunHeadingRadians());
    }

    public static CaRobotState getAnotherRobotState(CaRobot me, CaRobot prevState, List<Event> events) {
        CaPoint pos = prevState.position;
        double velocity = prevState.velocity;
        double heading = prevState.heading;
        double energy = prevState.energy;
        boolean alive = true;
        double firePower = 0;
        double gunHeat = prevState.gunHeat - BattleConstants.gunCoolingRate;
        double expectedEnergy = energy;
        double takenDamage = 0;
        double givenDamage = 0;
        double hitDamage = 0;
        for (Event e : events) {
            if (e instanceof RobotDeathEvent) {
                alive = false;
            } else if (e instanceof ScannedRobotEvent) {
                final ScannedRobotEvent se = (ScannedRobotEvent) e;
                pos = me.getPosition().project(me.getHeading() + se.getBearingRadians(), se.getDistance());
                velocity = se.getVelocity();
                heading = se.getHeadingRadians();
                energy = se.getEnergy();
            } else if (e instanceof HitByBulletEvent) {
                givenDamage = ((HitByBulletEvent) e).getPower() * 3;
                expectedEnergy += givenDamage;
            } else if (e instanceof BulletHitEvent) {
                takenDamage = Rules.getBulletDamage(((BulletHitEvent) e).getBullet().getPower());
                expectedEnergy -= takenDamage;
            } else if (e instanceof HitRobotEvent) {
                hitDamage = CaConstants.ROBOT_HIT_DAMAGE;
                expectedEnergy -= hitDamage;
            }
        }

        final boolean isHitWall = isHitWall(prevState, pos, velocity, heading);
        if (isHitWall) {
            expectedEnergy -= Rules.getWallHitDamage(prevState.velocity + prevState.getAcceleration());
        }

        if (gunHeat - BattleConstants.gunCoolingRate <= 0 && energy < expectedEnergy) {
            firePower = expectedEnergy - energy;
            gunHeat = Rules.getGunHeat(firePower);
        } else {
            gunHeat = max(0, prevState.gunHeat - BattleConstants.gunCoolingRate);
        }

        return new CaRobotState(prevState.getName(), pos, velocity, heading, energy, events.get(0).getTime(), alive, firePower, gunHeat);
    }

    private static boolean isHitWall(CaRobot prevState, CaPoint curPos, double curVelocity, double curHeading) {
        if (prevState == null) {
            return false;
        }

        if (abs(prevState.getVelocity()) - abs(curVelocity) > Rules.DECELERATION) {
            return true;
        }

        return prevState.getPosition().distance(curPos) -
                prevState.getPosition().distance(prevState.getPosition().project(curHeading, curVelocity)) < -1.1;
    }

}
