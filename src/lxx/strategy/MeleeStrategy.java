package lxx.strategy;

import lxx.BattleConstants;
import lxx.ConceptA;
import lxx.model.BattleField;
import lxx.model.BattleModel;
import lxx.model.CaRobot;
import lxx.movement.MovementDecision;
import lxx.radar.MeleeRadar;
import lxx.util.CaPoint;
import lxx.util.CaUtils;
import robocode.Rules;
import robocode.util.Utils;

import static java.lang.Math.min;
import static java.lang.Math.random;
import static java.lang.Math.signum;

/**
 * User: Aleksey Zhidkov
 * Date: 02.07.12
 */
public class MeleeStrategy implements Strategy {

    private final MeleeRadar radar = new MeleeRadar();

    private final ConceptA robot;

    private CaPoint destination;

    public MeleeStrategy(ConceptA robot) {
        this.robot = robot;
    }

    @Override
    public boolean applicable(BattleModel model) {
        return model.aliveEnemies.size() > 1;
    }

    @Override
    public TurnDecision getTurnDecision(BattleModel model) {
        robot.setAdjustGunForRobotTurn(true);
        robot.setAdjustRadarForGunTurn(true);
        robot.setAdjustRadarForRobotTurn(true);

        final CaRobot me = model.me;
        if (destination == null || me.getPosition().distance(destination) < 8 || getMinDistance(model) < 100) {
            selectDestination(model);
        }

        final MovementDecision md = MovementDecision.getMovementDecision(me, destination);

        final double radarTurnRate = radar.getRadarTurnRate(model);

        final double gunTurnRate;
        if (model.me.getGunHeat() / BattleConstants.gunCoolingRate > 10) {
            robot.setAdjustRadarForGunTurn(false);
            robot.setAdjustRadarForRobotTurn(false);
            gunTurnRate = Rules.GUN_TURN_RATE_RADIANS * signum(radarTurnRate);
        } else {
            gunTurnRate = Utils.normalRelativeAngle(getFireAngle(model) - me.getGunHeading());
        }

        return new TurnDecision(md.desiredVelocity, md.turnRate, gunTurnRate, 3, radarTurnRate);
    }

    private double getMinDistance(BattleModel model) {
        double minDistance = Integer.MAX_VALUE;

        for (CaRobot enemy : model.aliveEnemies) {
            minDistance = min(minDistance, model.me.getPosition().distance(enemy.getPosition()));
        }

        return minDistance;
    }

    private double getFireAngle(BattleModel model) {
        CaRobot bestEnemy = null;
        double bestEnemyScore = 0;
        for (CaRobot enemy : model.aliveEnemies) {
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
        for (CaRobot anotherEnemy : model.aliveEnemies) {
            if (enemy.equals(anotherEnemy)) {
                continue;
            }
            score += CaUtils.anglesDiff(angleToEnemy, model.me.angleTo(anotherEnemy)) / model.me.getPosition().distance(enemy.getPosition()) / (enemy.getEnergy() + 1);
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
        for (CaRobot enemy : model.aliveEnemies) {
            dng += enemy.getEnergy() / enemy.getPosition().distance(cnd);
        }
        return dng;
    }

}
