package lxx.movement;

import lxx.model.BattleField;
import lxx.model.BattleModel;
import lxx.model.CaRobot;
import lxx.paint.Arrow;
import lxx.paint.Canvas;
import lxx.util.CaConstants;
import lxx.util.CaPoint;
import lxx.util.CaUtils;
import robocode.Rules;
import robocode.util.Utils;

import java.awt.*;

import static java.lang.Math.abs;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class OrbitalMovement {

    public static final int ARROW_LENGTH = 70;
    private final double desiredDistance;

    public OrbitalMovement(double desiredDistance) {
        this.desiredDistance = desiredDistance;
    }

    public MovementDecision makeDecision(BattleModel model, CaPoint center, OrbitDirection direction) {
        double desiredHeading = getDesiredHeading(model, center, direction);

        final CaPoint myPos = model.me.getPosition();
        if (Canvas.RANDOM_MOVEMENT.enabled()) {
            Canvas.RANDOM_MOVEMENT.draw(new Arrow(myPos, desiredHeading, ARROW_LENGTH, 7), Color.RED);
        }

        // todo: add special case for OrbitDirection.STOP
        desiredHeading = BattleField.smoothWalls(myPos, desiredHeading, direction == OrbitDirection.CLOCKWISE);

        if (Canvas.RANDOM_MOVEMENT.enabled()) {
            Canvas.RANDOM_MOVEMENT.draw(new Arrow(myPos, desiredHeading, ARROW_LENGTH, 7), Color.GREEN);
        }

        return toMovementDecision(model.me, getDesiredSpeed(direction), desiredHeading);
    }

    public static MovementDecision toMovementDecision(CaRobot robot, double desiredSpeed, double desiredHeading) {
        final double headingRadians = robot.getHeading();
        final boolean wantToGoFront = CaUtils.anglesDiff(headingRadians, desiredHeading) < CaConstants.RADIANS_90;
        final double normalizedDesiredHeading = wantToGoFront ? desiredHeading : Utils.normalAbsoluteAngle(desiredHeading + CaConstants.RADIANS_180);

        final double turnRemaining = Utils.normalRelativeAngle(normalizedDesiredHeading - headingRadians);
        final double speed = robot.getSpeed();
        final double turnRateRadiansLimit = Rules.getTurnRateRadians(speed);
        final double turnRate =
                CaUtils.limit(-turnRateRadiansLimit,
                        turnRemaining,
                        turnRateRadiansLimit);

        return new MovementDecision(desiredSpeed * (wantToGoFront ? 1 : -1), turnRate);
    }

    private double getDesiredSpeed(OrbitDirection direction) {
        final double desiredVelocity;
        if (direction == OrbitDirection.STOP) {
            desiredVelocity = 0;
        } else {
            desiredVelocity = Rules.MAX_VELOCITY;
        }

        return desiredVelocity;
    }

    private double getDesiredHeading(BattleModel model, CaPoint center, OrbitDirection direction) {
        final CaRobot me = model.me;

        if (direction == OrbitDirection.STOP) {
            return me.getHeading();
        }

        final double distanceBetween = me.getPosition().distance(center);

        final double distanceDiff = distanceBetween - desiredDistance;
        final double attackAngleKoeff = distanceDiff / desiredDistance;

        final double maxAttackAngle = CaConstants.RADIANS_100;
        final double minAttackAngle = CaConstants.RADIANS_80;
        final double attackAngle = CaConstants.RADIANS_90 + (CaConstants.RADIANS_30 * attackAngleKoeff);

        final double angleToMe = CaUtils.angle(center.x, center.y, me.getPosition().x, me.getPosition().y);

        if (Canvas.RANDOM_MOVEMENT.enabled()) {
            Canvas.RANDOM_MOVEMENT.draw(new Arrow(me.getPosition(), angleToMe + CaConstants.RADIANS_90 * direction.direction, ARROW_LENGTH, 7), Color.WHITE);
        }

        return Utils.normalAbsoluteAngle(angleToMe +
                CaUtils.limit(minAttackAngle, attackAngle, maxAttackAngle) * direction.direction);
    }

}
