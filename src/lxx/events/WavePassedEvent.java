package lxx.events;

import lxx.model.CaRobot;
import lxx.model.Wave;
import lxx.util.IntervalDouble;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class WavePassedEvent {

    public final Wave wave;
    public final CaRobot passedRobot;
    public final IntervalDouble hitInterval;

    public WavePassedEvent(Wave wave, CaRobot passedRobot, IntervalDouble hitInterval) {
        this.wave = wave;
        this.passedRobot = passedRobot;
        this.hitInterval = hitInterval;
    }
}
