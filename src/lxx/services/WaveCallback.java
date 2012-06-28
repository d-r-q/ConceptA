package lxx.services;

import lxx.model.CaRobot;
import lxx.model.Wave;
import lxx.util.IntervalDouble;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public interface WaveCallback {

    void wavePassed(Wave w, CaRobot passedRobot, IntervalDouble hitInterval);

}
