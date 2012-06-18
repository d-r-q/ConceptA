package lxx.strategy;

import lxx.ConceptA;
import lxx.model.BattleModel;
import robocode.AdvancedRobot;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class StrategySelector {

    private final List<Strategy> strategies = new LinkedList<Strategy>();

    private final BattleModel model;

    public StrategySelector(BattleModel model, ConceptA me) {
        this.model = model;

        strategies.add(new FindEnemyStrategy());
        strategies.add(new DuelStrategy(me));
    }

    public Strategy selectStrategy() {

        for (Strategy s : strategies) {
            if (s.applicable(model)) {
                return s;
            }
        }

        return null;
    }

}
