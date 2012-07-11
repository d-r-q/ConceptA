package lxx.model;

import lxx.util.CaConstants;
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
    private final double movementDirection;

    public CaRobot(CaRobotState currentState) {
        super(currentState.name, currentState.position, currentState.velocity, currentState.heading, currentState.energy,
                currentState.lastScanTime, currentState.time, currentState.alive, currentState.firePower, currentState.gunHeat,
                currentState.radarHeading, currentState.gunHeading);

        acceleration = 0;
        movementDirection = currentState.heading;
    }

    public CaRobot(CaRobot prevState, CaRobotState currentState) {
        super(currentState.name, currentState.position, currentState.velocity, currentState.heading, currentState.energy,
                currentState.lastScanTime, currentState.time, currentState.alive, currentState.firePower, currentState.gunHeat,
                currentState.radarHeading, currentState.gunHeading);

        acceleration = calculateAcceleration(prevState, currentState);
        if (currentState.speed == 0) {
            movementDirection = Double.NaN;
        } else if (currentState.velocity > 0) {
            movementDirection = currentState.heading;
        } else {
            movementDirection = Utils.normalAbsoluteAngle(currentState.heading + CaConstants.RADIANS_180);
        }
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getGunHeat() {
        return gunHeat;
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
            if (prevState.lastScanTime + 1 == curState.lastScanTime && Log.isWarnEnabled()) {
                // todo: add check for wall hits
                Log.warn(curState.getName() + "'s acceleration is " + acceleration);
            }
            acceleration = CaUtils.limit(Rules.DECELERATION, acceleration, Rules.ACCELERATION);
        }

        return acceleration;
    }

    public double getMovementDirection() {
        return movementDirection;
    }

}
