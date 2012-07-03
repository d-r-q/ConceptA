package lxx.data;

import lxx.model.BattleModel;
import lxx.model.BattleModelListener;
import lxx.model.CaRobot;
import lxx.model.Wave;
import lxx.services.WaveCallback;
import lxx.services.WavesService;
import lxx.util.IntervalDouble;
import robocode.Rules;

/**
 * User: Aleksey Zhidkov
 * Date: 03.07.12
 */
public class MovementDataManager implements BattleModelListener, WaveCallback {

    private final WavesService wavesService;

    public MovementDataManager(WavesService wavesService) {
        this.wavesService = wavesService;
    }

    @Override
    public void battleModelUpdated(BattleModel newState) {
        if (!newState.hasDuelOpponent()) {
            return;
        }

        if (newState.duelOpponent.getFirePower() > 0) {
            final double firedBulletSpeed = Rules.getBulletSpeed(newState.duelOpponent.getFirePower());
            wavesService.launchWave(newState.prevState, newState.prevState.duelOpponent, firedBulletSpeed, this, newState.me);
        }
    }

    @Override
    public void wavePassed(Wave w, CaRobot passedRobot, IntervalDouble hitInterval) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
