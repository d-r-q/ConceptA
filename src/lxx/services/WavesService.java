package lxx.services;

import lxx.events.WavePassedEvent;
import lxx.model.BattleModel;
import lxx.model.BattleModelListener;
import lxx.model.CaRobot;
import lxx.model.Wave;
import lxx.paint.Canvas;
import lxx.paint.Circle;
import lxx.util.CaPoint;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class WavesService implements BattleModelListener {

    public static final Color WAVE_COLOR = new Color(0, 200, 255, 135);
    private LinkedList<W> waves = new LinkedList<W>();

    public void launchWave(BattleModel fireTimeState, CaPoint startPos, double speed, WaveCallback waveCallback, CaRobot... targets) {
        final Wave w = new Wave(fireTimeState, startPos, speed, targets);
        waves.add(new W(w, waveCallback));
    }

    @Override
    public void battleModelUpdated(BattleModel newState) {
        for (Iterator<W> iter = waves.iterator(); iter.hasNext();) {
            final W w = iter.next();
            final List<WavePassedEvent> wavePassedEvents = w.w.check(newState);
            for (WavePassedEvent e : wavePassedEvents) {
                for (WaveCallback wc : w.wcs) {
                    wc.wavePassed(w.w, e.passedRobot, e.hitInterval);
                }
            }
            if (Canvas.WAVES.enabled()) {
                Canvas.WAVES.draw(new Circle(w.w.startPos, w.w.getTravelledDistance()), WAVE_COLOR);
            }

            if (!w.w.hasRemainingTargets()) {
                iter.remove();
            }
        }
    }

    private final class W {

        public final List<WaveCallback> wcs = new LinkedList<WaveCallback>();
        public final Wave w;

        private W(Wave w, WaveCallback wc) {
            this.w = w;
            this.wcs.add(wc);
        }
    }

}
