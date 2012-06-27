package lxx.model;

import lxx.BattleConstants;
import lxx.util.CaUtils;
import lxx.util.Log;
import robocode.Rules;
import robocode.util.Utils;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.signum;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaRobot extends CaRobotState {

    private final double acceleration;
    private final double gunHeat;
    private final double absoluteHeading;
    private final double firedBulletSpeed;

    public CaRobot(CaRobotState currentState) {
        super(currentState.name, currentState.position, currentState.velocity, currentState.heading, currentState.energy, currentState.time);

        acceleration = 0;
        absoluteHeading = currentState.heading;
        gunHeat = BattleConstants.initialGunHeat;
        firedBulletSpeed = 0;
    }

    public CaRobot(CaRobot prevState, CaRobotState currentState) {
        super(currentState.name, currentState.position, currentState.velocity, currentState.heading, currentState.energy, currentState.time);

        acceleration = calculateAcceleration(prevState, currentState);
        absoluteHeading = currentState.velocity >= 0
                ? currentState.heading
                : Utils.normalAbsoluteAngle(currentState.heading + Math.PI);

        // todo: add hits accounting
        if (prevState.gunHeat - BattleConstants.gunCoolingRate <= 0 && currentState.energy < prevState.energy) {
            final double bulletPower = prevState.energy - currentState.energy;
            firedBulletSpeed = Rules.getBulletSpeed(bulletPower);
            gunHeat = Rules.getGunHeat(bulletPower);
        } else {
            firedBulletSpeed = 0;
            gunHeat = max(0, prevState.gunHeat - BattleConstants.gunCoolingRate);
        }
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getGunHeat() {
        return gunHeat;
    }

    public double getFiredBulletSpeed() {
        return firedBulletSpeed;
    }

    private static double calculateAcceleration(CaRobotState prevState, CaRobotState curState) {
        if (prevState == null) {
            return 0;
        }

        double acceleration;
        if (signum(curState.getVelocity()) == signum(prevState.getVelocity()) || abs(curState.getVelocity()) < 0.001) {
            acceleration = abs(curState.getVelocity()) - abs(prevState.getVelocity());
        } else {
            acceleration = abs(curState.getVelocity());
        }

        if (acceleration < -Rules.DECELERATION || acceleration > Rules.ACCELERATION) {
            if (prevState.time + 1 == curState.time && Log.isWarnEnabled()) {
                // todo: add check for wall hits
                Log.warn(curState.getName() + "'s acceleration is " + acceleration);
            }
            acceleration = CaUtils.limit(Rules.DECELERATION, acceleration, Rules.ACCELERATION);
        }

        return acceleration;
    }

    public double getAbsoluteHeading() {
        return absoluteHeading;
    }
}
