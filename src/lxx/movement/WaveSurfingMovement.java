package lxx.movement;

import lxx.BattleConstants;
import lxx.data.BearingOffsetDanger;
import lxx.data.MovementDataManager;
import lxx.model.*;
import lxx.paint.Canvas;
import lxx.paint.Circle;
import lxx.services.BulletsService;
import lxx.util.CaPoint;
import lxx.util.CaUtils;
import lxx.util.IntervalDouble;
import robocode.Rules;
import robocode.util.Utils;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static java.lang.Math.signum;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.min;

/**
 * User: jdev
 * Date: 08.07.12
 */
public class WaveSurfingMovement {

    private static final int MIN_WAVE_TRAVEL_TIME = 2;

    private static final Map<OrbitDirection, Color> predictionColors = new HashMap<OrbitDirection, Color>() {{
        put(OrbitDirection.CLOCKWISE, Color.GREEN);
        put(OrbitDirection.COUNTER_CLOCKWISE, Color.RED);
    }};

    private final MovementDataManager movementDataManager;
    private final BulletsService bulletsService;
    private final OrbitalMovement orbitalMovement;

    private OrbitDirection lastOrbitDirection;

    public WaveSurfingMovement(MovementDataManager movementDataManager, BulletsService bulletsService) {
        this.movementDataManager = movementDataManager;
        this.bulletsService = bulletsService;
        orbitalMovement = new OrbitalMovement(650);
    }

    public MovementDecision getMovementDecision(BattleModel model) {
        final Wave closestWave = bulletsService.getClosestDuelWave(model.duelOpponent.getName(), model.me.getPosition(), MIN_WAVE_TRAVEL_TIME);
        lastOrbitDirection = selectOrbitDirection(model, closestWave);
        final CaPoint orbitCenter;
        if (model.hasDuelOpponent()) {
            orbitCenter = model.duelOpponent.getPosition();
        } else {
            orbitCenter = closestWave.startPos;
        }

        return orbitalMovement.makeDecision(model.me, orbitCenter, lastOrbitDirection);
    }

    private OrbitDirection selectOrbitDirection(BattleModel model, final Wave closestWave) {
        double minDanger = Integer.MAX_VALUE;
        OrbitDirection bestOrbitDirection = null;
        for (OrbitDirection od : OrbitDirection.values()) {
            final Prediction pred = predictMovement(model.me, model.duelOpponent, closestWave, od);
            final double dng = getDanger(closestWave, pred) * (od == lastOrbitDirection ? 0.95 : 1);
            if (dng < minDanger) {
                minDanger = dng;
                bestOrbitDirection = od;
            }
            if (od != OrbitDirection.STOP && Canvas.WAVE_SURFING.enabled()) {
                final Color c = predictionColors.get(od);
                for (CaPoint pnt : pred.positions) {
                    Canvas.WAVE_SURFING.draw(new Circle(pnt, 2, true), c);
                }
            }
        }
        return bestOrbitDirection;
    }

    private double getDanger(Wave w, Prediction pred) {
        return getBulletDanger(w, pred.positions.getLast()) +
                1000 / pred.minDistanceBetween;
    }

    private double getBulletDanger(Wave w, CaPoint pnt) {
        final double robotRadialHalfWidth = CaUtils.getRobotWidthInRadians(w.startPos, pnt);
        final double currentBo = Utils.normalRelativeAngle(w.startPos.angleTo(pnt) - w.getAngleToTarget(BattleConstants.myName));
        final IntervalDouble effectiveIval = new IntervalDouble(currentBo - robotRadialHalfWidth, currentBo + robotRadialHalfWidth);

        double danger = 0;
        for (BearingOffsetDanger bo : movementDataManager.getBullets(w)) {
            if (bo.bo > effectiveIval.b) {
                break;
            } else if (bo.bo >= effectiveIval.a) {
                danger += (1 - abs(bo.bo - currentBo) / robotRadialHalfWidth) * bo.danger;
            }
        }

        return danger;
    }

    private Prediction predictMovement(CaRobot me, CaRobot enemy, Wave w, OrbitDirection orbitDirection) {
        final MovementDecision enemyMd = enemy != null
                ? new MovementDecision(Rules.MAX_VELOCITY * signum(enemy.getVelocity()), 0)
                : null;

        double travelledDist = w.getTravelledDistance();
        final Prediction prediction = new Prediction();
        prediction.positions.add(me.getPosition());
        while (w.startPos.distance(me.getPosition()) > travelledDist + w.speed * MIN_WAVE_TRAVEL_TIME) {
            final CaRobotState nextState = CaRobotStateFactory.apply(me, orbitalMovement.makeDecision(me, w.startPos, orbitDirection));
            me = new CaRobot(me, nextState);
            prediction.positions.add(me.getPosition());
            if (enemy != null) {
                final CaRobotState enemyNextState = CaRobotStateFactory.apply(enemy, enemyMd);
                enemy = new CaRobot(enemy, enemyNextState);
                prediction.minDistanceBetween = min(prediction.minDistanceBetween, me.getPosition().distance(enemy.getPosition()));
            }
            travelledDist += w.speed;
        }

        return prediction;
    }

    public boolean applicable(BattleModel model) {
        return bulletsService.getClosestDuelWave(model.duelOpponent.getName(), model.me.getPosition(), MIN_WAVE_TRAVEL_TIME) != null;
    }

    private class Prediction {

        private final LinkedList<CaPoint> positions = new LinkedList<CaPoint>();

        private double minDistanceBetween = Integer.MAX_VALUE;

    }

}
