package lxx.strategy;

import lxx.model.BattleModel;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public interface Strategy {

    boolean applicable(BattleModel model);

    TurnDecision getTurnDecision(BattleModel model);

}
