package lxx.strategy;

import lxx.BattleConstants;
import lxx.ConceptA;
import lxx.model.BattleModel;
import robocode.Rules;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class FindEnemyStrategy implements Strategy {

    private final ConceptA me;

    public FindEnemyStrategy(ConceptA conceptA) {
        this.me = conceptA;
    }

    @Override
    public boolean applicable(BattleModel model) {
        return model.getScannedEnemiesCount() < BattleConstants.totalEnemies && ConceptA.currentTime < 5;
    }

    @Override
    public TurnDecision getTurnDecision(BattleModel model) {
        me.setAdjustGunForRobotTurn(false);
        me.setAdjustRadarForGunTurn(false);
        me.setAdjustRadarForRobotTurn(false);
        return new TurnDecision(0, Rules.getTurnRateRadians(0), Rules.GUN_TURN_RATE_RADIANS, 0, Rules.RADAR_TURN_RATE_RADIANS);
    }

}
