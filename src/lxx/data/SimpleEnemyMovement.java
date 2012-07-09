package lxx.data;

import lxx.model.BattleField;
import lxx.model.BattleModel;
import lxx.model.CaRobot;
import lxx.util.CaConstants;
import robocode.util.Utils;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class SimpleEnemyMovement implements LocationFactory {

    private final String targetName;
    private final String attackerName;

    public SimpleEnemyMovement(String targetName, String attackerName) {
        this.targetName = targetName;
        this.attackerName = attackerName;
    }

    @Override
    public int getDimensions() {
        return 4;
    }

    @Override
    public double[] getLocation(BattleModel battleModel) {
        final CaRobot enemy = battleModel.getRobot(targetName);
        final double movementDirection;
        if (Double.isNaN(enemy.getMovementDirection())) {
            movementDirection = enemy.getHeading();
        } else {
            movementDirection = enemy.getMovementDirection();
        }
        final CaRobot me = battleModel.getRobot(attackerName);
        return new double[]{
                me.getPosition().distance(enemy.getPosition()) / BattleField.diagonal * 3,
                enemy.getSpeed() / 8 * 3,
                Utils.normalRelativeAngle(movementDirection - me.angleTo(enemy)) / CaConstants.RADIANS_180 * 2,
                BattleField.getDistanceToWall(enemy.getPosition(), movementDirection) / BattleField.diagonal
        };
    }
}
