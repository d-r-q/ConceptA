package lxx.gun;

import ags.utils.KdTree;
import lxx.BattleConstants;
import lxx.data.KnnDataSource;
import lxx.data.SimpleEnemyMovement;
import lxx.model.BattleModel;
import lxx.model.BattleModelListener;
import lxx.model.CaRobot;
import lxx.model.Wave;
import lxx.services.WaveCallback;
import lxx.services.WavesService;
import lxx.util.CaUtils;
import lxx.util.IntervalDouble;
import robocode.Rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class GuessFactorGun implements BattleModelListener, WaveCallback {

    private static final Map<String, KnnDataSource<Double>> dataSources = new HashMap<String, KnnDataSource<Double>>();

    private final WavesService wavesService;

    public GuessFactorGun(WavesService wavesService) {
        this.wavesService = wavesService;
    }

    public double aim(BattleModel battleModel, double bulletSpeed) {
        final KnnDataSource<Double> dataSource = dataSources.get(battleModel.duelOpponent.getName());
        if (dataSource == null || battleModel.duelOpponent.getEnergy() == 0) {
            return 0;
        }
        final List<KdTree.Entry<Double>> entries = dataSource.get(battleModel);
        if (entries.size() == 0) {
            return 0;
        }
        double totalBOs = 0;
        for (KdTree.Entry<Double> e : entries) {
            totalBOs += e.value * CaUtils.getMaxEscapeAngle(bulletSpeed) * CaUtils.getNonZeroLateralDirection(battleModel.me.getPosition(), battleModel.duelOpponent);
        }

        return totalBOs / entries.size();
    }

    @Override
    public void battleModelUpdated(BattleModel newState) {
        if (!newState.hasDuelOpponent()) {
            return;
        }

        if (newState.me.getFirePower() > 0) {
            final double firedBulletSpeed = Rules.getBulletSpeed(newState.me.getFirePower());
            wavesService.launchWave(newState.prevState, newState.prevState.me, firedBulletSpeed, this, newState.duelOpponent);
        }
    }

    @Override
    public void wavePassed(Wave w, CaRobot passedRobot, IntervalDouble hitInterval) {
        final double fireTimeLatDir = CaUtils.getNonZeroLateralDirection(w.fireTimeState.me.getPosition(), w.fireTimeState.duelOpponent);
        KnnDataSource<Double> dataSource = dataSources.get(passedRobot.getName());
        if (dataSource == null) {
            dataSource = new KnnDataSource<Double>(new SimpleEnemyMovement(passedRobot.getName(), BattleConstants.myName), 10000);
            dataSources.put(passedRobot.getName(), dataSource);
        }
        dataSource.add(w.fireTimeState, hitInterval.center() / CaUtils.getMaxEscapeAngle(w.speed) * fireTimeLatDir);
    }
}
