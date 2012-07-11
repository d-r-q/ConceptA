package lxx.services;

import lxx.model.BattleModel;
import lxx.model.BattleModelListener;
import lxx.model.CaRobot;
import lxx.model.Wave;
import lxx.paint.Canvas;
import lxx.paint.Circle;
import lxx.paint.Line;
import lxx.util.CaPoint;
import lxx.util.IntervalDouble;
import lxx.util.Log;
import robocode.Bullet;
import robocode.BulletHitBulletEvent;
import robocode.HitByBulletEvent;
import robocode.Rules;
import robocode.util.Utils;

import java.awt.*;
import java.util.LinkedList;

/**
 * User: Aleksey Zhidkov
 * Date: 04.07.12
 */
public class BulletsService implements BattleModelListener, WaveCallback {

    private final LinkedList<Wave> waves = new LinkedList<Wave>();
    private final LinkedList<BulletsServiceListener> listeners = new LinkedList<BulletsServiceListener>();

    private final WavesService wavesService;

    public BulletsService(WavesService wavesService) {
        this.wavesService = wavesService;
    }

    @Override
    public void battleModelUpdated(BattleModel newState) {
        for (CaRobot enemy : newState.aliveEnemies) {
            if (enemy.getLastScanTime() == newState.time && enemy.getFirePower() > 0) {
                final Wave w = wavesService.launchWave(newState.prevState, newState.prevState.getRobot(enemy.getName()), Rules.getBulletSpeed(enemy.getFirePower()), this, newState.me);
                waves.add(w);
                for (BulletsServiceListener listener : listeners) {
                    listener.bulletFired(w);
                }
            }
        }
    }

    public void onBulletHitBullet(BulletHitBulletEvent e) {
        final Wave w = getWave(e.getHitBullet());

        if (w == null) {
            if (Log.isWarnEnabled()) {
                Log.warn("No wave for bullet found");
            }
            return;
        }

        waves.remove(w);

        for (BulletsServiceListener listener : listeners) {
            listener.bulletIntercepted(w, e.getHitBullet());
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        final Wave w = getWave(e.getBullet());

        if (w == null) {
            if (Log.isWarnEnabled()) {
                Log.warn("No wave for bullet found");
            }
            return;
        }

        waves.remove(w);

        for (BulletsServiceListener listener : listeners) {
            listener.bulletHit(w, e.getBullet());
        }
    }

    private Wave getWave(Bullet bullet) {
        for (Wave w : waves) {
            if (!w.owner.getName().equals(bullet.getName())) {
                continue;
            }

            if (!Utils.isNear(w.speed, bullet.getVelocity())) {
                continue;
            }

            final CaPoint wavePos = w.startPos.project(bullet.getHeadingRadians(), w.getTravelledDistance());

            if (Canvas.BULLET_HITS.enabled()) {
                Canvas.BULLET_HITS.reset();
                Canvas.BULLET_HITS.draw(new Line(w.startPos, w.startPos.project(bullet.getHeadingRadians(), w.getTravelledDistance())), Color.WHITE);
                Canvas.BULLET_HITS.draw(new Circle(wavePos, 7), Color.GREEN);
                Canvas.BULLET_HITS.draw(new Circle(new CaPoint(bullet.getX(), bullet.getY()), 7), Color.RED);
            }

            if (wavePos.distance(bullet.getX(), bullet.getY()) > bullet.getVelocity() + 1) {
                continue;
            }

            return w;
        }

        return null;
    }

    @Override
    public void wavePassed(Wave w, CaRobot passedRobot, IntervalDouble hitInterval) {
        if (!w.hasRemainingTargets()) {
            waves.remove(w);
            for (BulletsServiceListener listener : listeners) {
                listener.bulletMissed(w);
            }
        }
    }

    public void addListener(BulletsServiceListener listener) {
        listeners.add(listener);
    }

    public Wave getClosestDuelWave(String owner, CaPoint pnt, double travelTimeLimit) {
        Wave closestWave = null;
        double minTT = Integer.MAX_VALUE;
        for (Wave w : waves) {
            if (!owner.equals(w.owner.getName())) {
                continue;
            }
            final double dst = w.startPos.distance(pnt);
            if (w.getTravelledDistance() > dst) {
                continue;
            }
            final double tt = (dst - w.getTravelledDistance()) / w.speed;

            if (tt <= travelTimeLimit) {
                continue;
            }

            if (!w.aimTimeState.hasDuelOpponent()) {
                continue;
            }

            if (tt < minTT) {
                minTT = tt;
                closestWave = w;
            }
        }

        return closestWave;
    }

}