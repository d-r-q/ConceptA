package lxx.gun;

import lxx.data.DataSource;
import lxx.data.KnnDataSource;
import lxx.model.BattleModel;
import lxx.model.BattleModelListener;
import lxx.model.CaRobot;
import lxx.model.Wave;
import lxx.services.WaveCallback;
import lxx.services.WavesService;
import lxx.util.IntervalDouble;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class GuessFactorGun implements BattleModelListener, WaveCallback {

    private static final DataSource dataSource = new KnnDataSource(new SimpleEnemyMovement());

    private final WavesService wavesService;

    public GuessFactorGun(WavesService wavesService) {
        this.wavesService = wavesService;
    }

    @Override
    public void battleModelUpdated(BattleModel newState) {
        if (!newState.hasDuelOpponent()) {
            return;
        }

        final double firedBulletSpeed = newState.me.getFiredBulletSpeed();
        if (firedBulletSpeed > 0) {
            wavesService.launchWave(newState.prevState.me.getPosition(), firedBulletSpeed, this, newState.duelOpponent);
        }
    }

    @Override
    public void wavePassed(Wave w, CaRobot passedRobot, IntervalDouble hitInterval) {
    }
}
