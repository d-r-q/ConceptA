package lxx.gun;

import lxx.BattleConstants;
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
        return 3;
    }

    @Override
    public double[] getLocation(BattleModel battleModel) {
        final CaRobot enemy = battleModel.duelOpponent;
        return new double[]{
                enemy.getSpeed() / 8,
                Utils.normalRelativeAngle(enemy.getAbsoluteHeading() - battleModel.me.getAbsoluteHeading()) / CaConstants.RADIANS_180,
                BattleField.getDistanceToWall(enemy.getPosition(), enemy.getAbsoluteHeading()) / BattleField.diagonal
        };
    }
}
