package lxx.strategy;

import lxx.model.BattleField;
import lxx.model.BattleModel;
import lxx.model.CaRobot;
import lxx.movement.MovementDecision;
import lxx.util.CaConstants;
import lxx.util.CaPoint;
import lxx.util.CaUtils;
import robocode.Rules;
import robocode.util.Utils;

import static java.lang.Math.random;

/**
 * User: Aleksey Zhidkov
 * Date: 02.07.12
 */
public class MeleeStrategy implements Strategy {

    private CaPoint destination;

    @Override
    public boolean applicable(BattleModel model) {
        return model.aliveEnemies.size() > 1;
    }

    @Override
    public TurnDecision getTurnDecision(BattleModel model) {
        final CaRobot me = model.me;
        if (destination == null || me.getPosition().distance(destination) < 8) {
            selectDestination(model);
        }

        final MovementDecision md = MovementDecision.getMovementDecision(me, destination);


        return new TurnDecision(md.desiredVelocity, md.turnRate, Utils.normalRelativeAngle(getFireAngle(model) - me.getGunHeading()), 3, Double.POSITIVE_INFINITY);
    }

    private double getFireAngle(BattleModel model) {
        CaRobot bestEnemy = null;
        double bestEnemyScore = 0;
        for (CaRobot enemy : model.enemies.values()) {
            final double enemyScore = getEnemyScore(enemy, model);
            if (bestEnemy == null || enemyScore > bestEnemyScore) {
                bestEnemy = enemy;
                bestEnemyScore = enemyScore;
            }
        }

        return model.me.getPosition().angleTo(bestEnemy.getPosition());
    }

    private double getEnemyScore(CaRobot enemy, BattleModel model) {
        double score = 0;
        final double angleToEnemy = model.me.angleTo(enemy);
        for (CaRobot anotherEnemy : model.enemies.values()) {
            if (enemy.equals(anotherEnemy)) {
                continue;
            }
            score += enemy.getEnergy() / CaUtils.anglesDiff(angleToEnemy, model.me.angleTo(anotherEnemy));
        }

        return score;
    }

    private void selectDestination(BattleModel model) {
        final int minDistance = 150;
        double minDanger = Integer.MAX_VALUE;
        CaPoint bestPoint = null;
        for (int i = 0; i < 180; i++) {
            final double dist = minDistance + 100 * random();
            final double alpha = Math.PI * 2 * random();
            final CaPoint cnd = model.me.project(alpha, dist);
            if (!BattleField.contains(cnd)) {
                continue;
            }
            final double cndDanger = getDanger(cnd, model);
            if (bestPoint == null || cndDanger < minDanger) {
                minDanger = cndDanger;
                bestPoint = cnd;
            }
        }
        destination = bestPoint;
    }

    private double getDanger(CaPoint cnd, BattleModel model) {
        double dng = 0;
        for (CaRobot enemy : model.enemies.values()) {
            dng += enemy.getEnergy() / enemy.getPosition().distance(cnd);
        }
        return dng;
    }

}
