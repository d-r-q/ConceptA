package lxx.strategy;

import lxx.BattleConstants;
import lxx.model.BattleModel;
import lxx.model.CaRobot;
import lxx.movement.MovementDecision;
import lxx.util.CaPoint;
import lxx.util.Log;
import robocode.util.Utils;

import static java.lang.Math.random;

/**
 * User: Aleksey Zhidkov
 * Date: 03.07.12
 */
public class TeamStrategy implements Strategy {


    @Override
    public boolean applicable(BattleModel model) {
        return model.aliveEnemies.size() > 0 && model.aliveTeammates.size() > 0;
    }

    @Override
    public TurnDecision getTurnDecision(BattleModel model) {
        final CaRobot bestTarget = selectTarget(model);
        final CaRobot me = model.me;
        final double angleToTarget = me.angleTo(bestTarget);

        final double alpha = (Math.PI * 2) / 5 * BattleConstants.myIndex;
        final double dist = 75 * (BattleConstants.myIndex + 1);
        final CaPoint destination = bestTarget.getPosition().project(alpha, dist);

        final MovementDecision md = MovementDecision.getMovementDecision(me, destination);
        return new TurnDecision(md.desiredVelocity, md.turnRate, Utils.normalRelativeAngle(angleToTarget - me.getGunHeading()), 3, Double.POSITIVE_INFINITY);
    }

    private CaRobot selectTarget(BattleModel model) {
        CaRobot bestTarget = null;
        double maxScore = 0;

        for (CaRobot enemy : model.aliveEnemies) {
            final double score = getTargetScore(enemy, model);
            if (bestTarget == null || score > maxScore) {
                maxScore = score;
                bestTarget = enemy;
            }
        }

        if (bestTarget == null) {
            if (Log.isWarnEnabled()) {
                Log.warn("TeamStrategy: bestTarget is null");
            }
        }

        return bestTarget;
    }

    private double getTargetScore(CaRobot target, BattleModel model) {
        double score = 0;

        for (CaRobot teammate : model.getTeamRobots()) {
            score += 1D / teammate.getPosition().distance(target.getPosition());
        }

        return score;
    }

}
