package lxx.movement;

import lxx.model.CaRobot;
import lxx.util.CaConstants;
import lxx.util.CaPoint;
import lxx.util.CaUtils;
import robocode.Rules;
import robocode.util.Utils;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class MovementDecision {

    public final double desiredVelocity;
    public final double turnRate;

    public MovementDecision(double desiredVelocity, double turnRate) {
        this.desiredVelocity = desiredVelocity;
        this.turnRate = turnRate;
    }

    public static MovementDecision getMovementDecision(CaRobot robot, CaPoint destination) {
        final double desiredDirection = robot.getPosition().angleTo(destination);
        final double desiredVelocity;
        final double turnRate;
        if (CaUtils.anglesDiff(robot.getHeading(), desiredDirection) < CaConstants.RADIANS_90) {
            desiredVelocity = robot.getPosition().distance(destination) > CaUtils.getStopDistance(robot.getSpeed())
                    ? Rules.MAX_VELOCITY
                    : 0;
            turnRate = Utils.normalRelativeAngle(desiredDirection - robot.getHeading());
        } else {
            desiredVelocity = robot.getPosition().distance(destination) > CaUtils.getStopDistance(robot.getSpeed())
                    ? -Rules.MAX_VELOCITY
                    : 0;
            turnRate = Utils.normalRelativeAngle(desiredDirection - Utils.normalAbsoluteAngle(robot.getHeading() + CaConstants.RADIANS_180));
        }

        return new MovementDecision(desiredVelocity, turnRate);
    }

}
