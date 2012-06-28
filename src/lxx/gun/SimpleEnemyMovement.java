package lxx.gun;

import lxx.data.LocationFactory;
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

    @Override
    public int getDimensions() {
        return 4;
    }

    @Override
    public double[] getLocation(BattleModel battleModel) {
        final CaRobot enemy = battleModel.duelOpponent;
        final double movementDirection;
        if (Double.isNaN(enemy.getMovementDirection())) {
            movementDirection = enemy.getHeading();
        } else {
            movementDirection = enemy.getMovementDirection();
        }
        return new double[]{
                battleModel.me.getPosition().distance(enemy.getPosition()) / BattleField.diagonal * 3,
                enemy.getSpeed() / 8 * 3,
                Utils.normalRelativeAngle(movementDirection - battleModel.me.angleTo(enemy)) / CaConstants.RADIANS_180 * 2,
                BattleField.getDistanceToWall(enemy.getPosition(), movementDirection) / BattleField.diagonal
        };
    }
}
