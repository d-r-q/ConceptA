package lxx.strategy;

import lxx.ConceptA;
import lxx.data.GuessFactor;
import lxx.data.MovementDataManager;
import lxx.gun.GuessFactorGun;
import lxx.model.BattleModel;
import lxx.services.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class StrategySelector {

    private final List<Strategy> strategies = new LinkedList<Strategy>();

    public StrategySelector(ConceptA me, Context context) {
        strategies.add(new WinStrategy());
        strategies.add(new FindEnemyStrategy(me));
        final GuessFactorGun gun = new GuessFactorGun(context.getWavesService());
        me.addBattleModelListener(gun);
        strategies.add(new DuelStrategy(me, context, gun));
        strategies.add(new TeamStrategy());
        strategies.add(new MeleeStrategy());
    }

    public Strategy selectStrategy(BattleModel model) {

        for (Strategy s : strategies) {
            if (s.applicable(model)) {
                return s;
            }
        }

        return null;
    }

}
