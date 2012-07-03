package lxx.strategy;

import lxx.model.BattleModel;

/**
 * User: Aleksey Zhidkov
 * Date: 03.07.12
 */
public class WinStrategy implements Strategy {
    @Override
    public boolean applicable(BattleModel model) {
        return model.getAliveEnemies().size() == 0;
    }

    @Override
    public TurnDecision getTurnDecision(BattleModel model) {
        return new TurnDecision(0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.1, Double.POSITIVE_INFINITY);
    }
}
